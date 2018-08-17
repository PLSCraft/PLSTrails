package today.pls.trails;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PLSTrails extends JavaPlugin {

    static final String TRAILS_MENU = "§b§lTrails";
    static final String PARTICLES_MENU = "§2§lParticles";

    static final String RAIN_CLOUD_MENU = "§b§lRain Cloud";
    static final String RAIN_ITEM_MENU = "§b§lRain Drops";

    static final String WING_TYPE_MENU = "§b§lWing Type";
    static final String INNER_WING_COLOR_MENU = "§b§lWings Inner Color";
    static final String OUTER_WING_COLOR_MENU = "§b§lWings Outer Color";
    static final String EDGE_WING_COLOR_MENU = "§b§lWings Edge Color";

    static final Material MENU_PARTICLE_MATERIAL = Material.FIREWORK;
    static final Material MENU_WINGS_MATERIAL = Material.FEATHER;
    static final Material MENU_RAIN_MATERIAL = Material.WOOL;

    static final Material MENU_STOP_MATERIAL = Material.SULPHUR;
    static final Material MENU_CLOSE_MATERIAL = Material.BARRIER;

    static final Material[] rainTypes = {
            Material.DIAMOND,Material.IRON_INGOT,Material.GOLD_INGOT,Material.GOLD_NUGGET,Material.EMERALD,
            Material.NETHER_STALK,Material.WHEAT,Material.MELON,Material.CARROT_ITEM,
            Material.GOLDEN_APPLE,Material.GOLDEN_CARROT,Material.SPECKLED_MELON
    };

    static final Material[] shinyRainTypes = {Material.GOLDEN_APPLE,Material.GOLDEN_CARROT,Material.SPECKLED_MELON};

    static PLSTrails instance;

    HashMap<UUID, Trail> playerTrails = new HashMap<>();
    ArrayList<Item> rainItems = new ArrayList<>();

    @Override
    public void onEnable() {
        super.onEnable();

        getServer().getPluginManager().registerEvents(new PLSListener(this), this);
        getCommand("trail").setExecutor(new TrailCommandExecutor(this));

        new TrailSpawner().runTaskTimer(this, 0, 2);

        instance = this;
    }

    @Override
    public void onDisable() {
        super.onDisable();

        Iterator<Item> itemIter = rainItems.iterator();
        while(itemIter.hasNext()){
            Item i = itemIter.next();
            i.remove();
            itemIter.remove();
        }
    }

    public Inventory getTrailsInventory(Entity e) {
        Inventory i = Bukkit.createInventory(null, 9*5, TRAILS_MENU);

        i.setItem(9+2,Utils.itemStackWithName(MENU_RAIN_MATERIAL, "§1§1Rain Trails"));
        i.setItem(9+4,Utils.itemStackWithName(MENU_PARTICLE_MATERIAL, "§1§lParticle Trails"));
        i.setItem(9+6,Utils.itemStackWithName(MENU_WINGS_MATERIAL, "§1§1Wing Trails"));

        i.setItem(9*3+3, Utils.itemStackWithName(MENU_STOP_MATERIAL, "§4§lClear Your Trails"));
        i.setItem(9*3+5, Utils.itemStackWithName(MENU_CLOSE_MATERIAL, "§4§lClose"));
        return i;
    }

    public Inventory getParticleTrailsInventory(Entity e) {
        ParticleData[] p = ParticleData.values();

        int invsize = (int) (Math.ceil((p.length+1)/9f)*9);

        Inventory i = Bukkit.createInventory(null, invsize, PARTICLES_MENU);

        for (ParticleData pd : p) {
            Material m = pd.menuMaterial;
            String n = "§3§l"+pd.particleName;


            i.addItem(Utils.itemStackWithName(m, n));
        }

        i.setItem(i.getSize()-1, Utils.itemStackWithName(MENU_CLOSE_MATERIAL, "§4§lMain Menu"));
        return i;
    }

    public Inventory getRainCloudInventory(Entity e){
        RainCloudType[] cloudTypes = RainCloudType.values();

        int invsize = (int) (Math.ceil((cloudTypes.length+1)/9f)*9);

        Inventory i = Bukkit.createInventory(null, invsize, RAIN_CLOUD_MENU);

        for (RainCloudType rct: cloudTypes) {
            ItemStack cloth = new ItemStack(Material.WOOL,1,(short) rct.color.getWoolData());
            cloth = Utils.setItemNameAndLore(cloth,rct.name,null);
            i.addItem(cloth);
        }

        i.setItem(8, Utils.itemStackWithName(MENU_CLOSE_MATERIAL, "§4§lMain Menu"));

        return i;
    }

    public Inventory getRainItemInventory(Entity e, RainCloudType cloud){
        int invsize = (int) (Math.ceil((rainTypes.length+2)/9f)*9);
        Inventory i = Bukkit.createInventory(null, invsize, RAIN_ITEM_MENU);

        for (Material m:rainTypes) {
            ItemStack is = Utils.itemStackWithName(m,"§2§l"+WordUtils.capitalizeFully(m.toString().replace("ITEM","").replace("_"," ").trim()));

            if(Arrays.asList(shinyRainTypes).indexOf(m)>=0){
                is = Utils.shiny(is);
            }

            i.addItem(is);
        }

        i.setItem(i.getSize()-2, Utils.shiny(Utils.setItemNameAndLore(cloud.getAsWool(), "§3§lChange Cloud",null)));
        i.setItem(i.getSize()-1, Utils.itemStackWithName(MENU_CLOSE_MATERIAL, "§4§lMain Menu"));

        return i;
    }

    public Inventory getWingTypeInventory(Entity e){
        int invsize = (int) (Math.ceil((WingType.values().length+2)/9f)*9);
        Inventory i = Bukkit.createInventory(null, invsize, WING_TYPE_MENU);

        for (WingType wt : WingType.values()) {
            ItemStack is = Utils.itemStackWithName(wt.menuMat,"§a§l"+WordUtils.capitalizeFully(wt.toString().replace("ITEM","").replace("_"," ").trim()));
            i.addItem(is);
        }

        i.setItem(i.getSize()-1, Utils.itemStackWithName(MENU_CLOSE_MATERIAL, "§4§lMain Menu"));
        return i;
    }

    public Inventory getWingColorInventory(Entity e, String name, ItemStack... prevs){
        int invsize = (int) (Math.ceil((DyeColor.values().length+prevs.length+3)/9f)*9);
        Inventory i = Bukkit.createInventory(null, invsize, name);

        for (DyeColor dc: DyeColor.values()) {
            ItemStack is = new ItemStack(Material.WOOL,1,dc.getWoolData());
            is = Utils.setItemNameAndLore(is,"§5§l"+WordUtils.capitalizeFully(dc.toString().replace("_"," ").trim()),name);
            i.addItem(is);
        }

        i.setItem(i.getSize()-1, Utils.itemStackWithName(MENU_CLOSE_MATERIAL, "§4§lMain Menu"));

        int c = 3;

        for (ItemStack is: prevs){
            i.setItem(i.getSize()-(c++),is);
        }


        return i;
    }


    private int ticks;

    class TrailSpawner extends BukkitRunnable {

        @Override
        public void run() {
            ticks++;
            if(ticks==100) ticks=0; //just so we never overflow even tho in reality its like 42 years anyways

            Iterator<Item> itemIter = rainItems.iterator();
            while(itemIter.hasNext()){
                Item i = itemIter.next();
                i.setVelocity(i.getVelocity().setY(-0.3f));

                if(i.isOnGround() || i.getTicksLived() >= 12020) {
                    i.remove();
                    itemIter.remove();
                }
            }

            for (Map.Entry<UUID,Trail> e : playerTrails.entrySet()) {
                Player p = getServer().getPlayer(e.getKey());

                if(p == null || !p.isOnline()){
                    playerTrails.remove(e.getKey());
                    continue;
                }

                e.getValue().draw(p,ticks);
            }
        }
    }

}
