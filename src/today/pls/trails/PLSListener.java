package today.pls.trails;

import net.minecraft.server.v1_12_R1.EnumParticle;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;

import java.util.Arrays;

public class PLSListener implements Listener {
    PLSTrails pl;
    public PLSListener(PLSTrails _pl) {
        pl = _pl;
    }

    @EventHandler
    public void onInventoryEvent(InventoryClickEvent ice){

        if(ice.getInventory() == null) return;
        if(ice.getCurrentItem() == null) return;
        if(ice.getCurrentItem().getData() == null) return;

        if(ice.getClickedInventory().getName().equals(PLSTrails.TRAILS_MENU)){
            ice.setCancelled(true);
            Material m = ice.getCurrentItem().getData().getItemType();

            if(m == PLSTrails.MENU_PARTICLE_MATERIAL){
                ice.getWhoClicked().openInventory(pl.getParticleTrailsInventory(ice.getWhoClicked()));
            }else if(m == PLSTrails.MENU_WINGS_MATERIAL) {
                ice.getWhoClicked().openInventory(pl.getWingTypeInventory(ice.getWhoClicked()));
                //  ice.getWhoClicked().openInventory(pl.getParticleTrailsInventory(ice.getWhoClicked()));
            }else if(m == PLSTrails.MENU_RAIN_MATERIAL) {
                ice.getWhoClicked().openInventory(pl.getRainCloudInventory(ice.getWhoClicked()));
            }else if(m == PLSTrails.MENU_STOP_MATERIAL){
                pl.playerTrails.remove(ice.getWhoClicked().getUniqueId());
                ice.getWhoClicked().closeInventory();
            }else if(m == PLSTrails.MENU_CLOSE_MATERIAL){
                ice.getWhoClicked().closeInventory();
            }

            return;
        }

        //PARTICLE SELECTION
        if(ice.getClickedInventory().getName().equals(PLSTrails.PARTICLES_MENU)){
            ice.setCancelled(true);

            Material m = ice.getCurrentItem().getData().getItemType();
            ParticleData pd = ParticleData.getDataFromMaterial(m);

            if(m == PLSTrails.MENU_CLOSE_MATERIAL){
                ice.getWhoClicked().openInventory(pl.getTrailsInventory(ice.getWhoClicked()));
            }else {
                pl.playerTrails.put(ice.getWhoClicked().getUniqueId(), new Trail(pd.particle));

                ice.getWhoClicked().sendMessage("Changed your trail to " + pd.particleName);
                ice.getWhoClicked().closeInventory();
            }

            return;
        }

        //RAIN SELECTION
        if(ice.getClickedInventory().getName().equals(PLSTrails.RAIN_CLOUD_MENU)){
            ice.setCancelled(true);
            Material m = ice.getCurrentItem().getData().getItemType();

            if(m == PLSTrails.MENU_CLOSE_MATERIAL){
                ice.getWhoClicked().openInventory(pl.getTrailsInventory(ice.getWhoClicked()));
            }else{
                ItemStack cloudItem = ice.getCurrentItem();
                if(cloudItem == null || !(cloudItem.getData() instanceof Wool)) return; //this should never happen.

                RainCloudType rct = RainCloudType.getByWool((Wool) cloudItem.getData());

                ice.getWhoClicked().openInventory(pl.getRainItemInventory(ice.getWhoClicked(), rct));
            }

            return;
        }

        if(ice.getClickedInventory().getName().equals(PLSTrails.RAIN_ITEM_MENU)){
            ice.setCancelled(true);
            Material m = ice.getCurrentItem().getData().getItemType();

            ItemStack cloudItem = ice.getClickedInventory().getItem(ice.getClickedInventory().getSize()-2);
            if(cloudItem == null || !(cloudItem.getData() instanceof Wool)) return; //this should never happen.

            RainCloudType rct = RainCloudType.getByWool((Wool) cloudItem.getData());

            if(rct == null) return; //should never happen

            if(cloudItem.equals(ice.getCurrentItem())){
                ice.getWhoClicked().openInventory(pl.getRainCloudInventory(ice.getWhoClicked()));
            }else if(m == PLSTrails.MENU_CLOSE_MATERIAL){
                ice.getWhoClicked().openInventory(pl.getTrailsInventory(ice.getWhoClicked()));
            }else if(Arrays.asList(PLSTrails.rainTypes).indexOf(m)>=0){
                pl.playerTrails.put(ice.getWhoClicked().getUniqueId(),new Trail(m,rct.particle));
                ice.getWhoClicked().closeInventory();
            }
            return;
        }

        //WINGS SELECTION
        if(ice.getClickedInventory().getName().equals(PLSTrails.WING_TYPE_MENU)){
            ice.setCancelled(true);
            Material m = ice.getCurrentItem().getData().getItemType();

            if(m == PLSTrails.MENU_CLOSE_MATERIAL){
                ice.getWhoClicked().openInventory(pl.getTrailsInventory(ice.getWhoClicked()));
            }else{
                ItemStack typeItem = ice.getCurrentItem();
                if(typeItem == null) return;

                WingType wt = WingType.getByMaterial(typeItem.getData().getItemType());
                if(wt == null) return; //this should never happen

                ice.getWhoClicked().openInventory(pl.getWingColorInventory(ice.getWhoClicked(), PLSTrails.INNER_WING_COLOR_MENU, typeItem));
            }
            return;
        }

        if(ice.getClickedInventory().getName().equals(PLSTrails.INNER_WING_COLOR_MENU)){
            ice.setCancelled(true);
            Material m = ice.getCurrentItem().getData().getItemType();

            ItemStack wingItem = ice.getClickedInventory().getItem(ice.getClickedInventory().getSize()-3);

            if(m == PLSTrails.MENU_CLOSE_MATERIAL) {
                ice.getWhoClicked().openInventory(pl.getTrailsInventory(ice.getWhoClicked()));
            }else if(ice.getCurrentItem().equals(wingItem)){
                ice.getWhoClicked().openInventory(pl.getWingTypeInventory(ice.getWhoClicked()));
            }else{
                ItemStack colorItem = ice.getCurrentItem();
                if(colorItem == null) return;

                ice.getWhoClicked().openInventory(pl.getWingColorInventory(ice.getWhoClicked(), PLSTrails.OUTER_WING_COLOR_MENU, wingItem, colorItem));
            }
            return;
        }

        if(ice.getClickedInventory().getName().equals(PLSTrails.OUTER_WING_COLOR_MENU)){
            ice.setCancelled(true);
            Material m = ice.getCurrentItem().getData().getItemType();

            ItemStack wingItem = ice.getClickedInventory().getItem(ice.getClickedInventory().getSize()-3);
            ItemStack innerColor = ice.getClickedInventory().getItem(ice.getClickedInventory().getSize()-4);

            if(m == PLSTrails.MENU_CLOSE_MATERIAL) {
                ice.getWhoClicked().openInventory(pl.getTrailsInventory(ice.getWhoClicked()));
            }else if(ice.getCurrentItem().equals(wingItem)){
                ice.getWhoClicked().openInventory(pl.getWingTypeInventory(ice.getWhoClicked()));
            }else if(ice.getCurrentItem().equals(innerColor)) {
                ice.getWhoClicked().openInventory(pl.getWingColorInventory(ice.getWhoClicked(), PLSTrails.INNER_WING_COLOR_MENU, wingItem));
            }else{
                ItemStack colorItem = ice.getCurrentItem();
                if(colorItem == null) return;

                ice.getWhoClicked().openInventory(pl.getWingColorInventory(ice.getWhoClicked(), PLSTrails.EDGE_WING_COLOR_MENU, wingItem, innerColor, colorItem));
            }
            return;
        }

        if(ice.getClickedInventory().getName().equals(PLSTrails.EDGE_WING_COLOR_MENU)){
            ice.setCancelled(true);
            Material m = ice.getCurrentItem().getData().getItemType();

            ItemStack wingItem = ice.getClickedInventory().getItem(ice.getClickedInventory().getSize()-3);
            ItemStack innerColor = ice.getClickedInventory().getItem(ice.getClickedInventory().getSize()-4);
            ItemStack outerColor = ice.getClickedInventory().getItem(ice.getClickedInventory().getSize()-5);

            if(m == PLSTrails.MENU_CLOSE_MATERIAL) {
                ice.getWhoClicked().openInventory(pl.getTrailsInventory(ice.getWhoClicked()));
            }else if(ice.getCurrentItem().equals(wingItem)){
                ice.getWhoClicked().openInventory(pl.getWingTypeInventory(ice.getWhoClicked()));
            }else if(ice.getCurrentItem().equals(innerColor)) {
                ice.getWhoClicked().openInventory(pl.getWingColorInventory(ice.getWhoClicked(), PLSTrails.INNER_WING_COLOR_MENU, wingItem));
            }else if(ice.getCurrentItem().equals(outerColor)) {
                ice.getWhoClicked().openInventory(pl.getWingColorInventory(ice.getWhoClicked(), PLSTrails.OUTER_WING_COLOR_MENU, wingItem, innerColor));
            }else{
                WingType wt = WingType.getByMaterial(wingItem.getData().getItemType());
                DyeColor c1 = ((Wool) innerColor.getData()).getColor();
                DyeColor c2 = ((Wool) outerColor.getData()).getColor();
                DyeColor c3 = ((Wool) ice.getCurrentItem().getData()).getColor();

                pl.playerTrails.put(ice.getWhoClicked().getUniqueId(),new Trail(wt,c1,c2,c3));

                ice.getWhoClicked().closeInventory();
            }
            return;
        }
    }

    @EventHandler
    public void onEntityPickup(EntityPickupItemEvent e){
        if(e.getItem() == null) return;

        ItemStack is = e.getItem().getItemStack();

        if(is.hasItemMeta()){
            ItemMeta im = is.getItemMeta();
            if(im.hasDisplayName() && im.getDisplayName().equals("§r§0RAIN_ITEM_NPU")){
                e.setCancelled(true);
                e.getItem().remove();
            }
        }
    }

    public void onHopperPickup(InventoryPickupItemEvent e){
        if(e.getItem() == null) return;

        ItemStack is = e.getItem().getItemStack();

        if(is.hasItemMeta()){
            ItemMeta im = is.getItemMeta();
            if(im.hasDisplayName() && im.getDisplayName().equals("§r§0RAIN_ITEM_NPU")){
                e.setCancelled(true);
                e.getItem().remove();
            }
        }
    }
}
