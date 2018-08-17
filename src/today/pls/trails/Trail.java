package today.pls.trails;


import net.minecraft.server.v1_12_R1.EnumParticle;
import net.minecraft.server.v1_12_R1.PacketPlayOutWorldParticles;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.function.Consumer;

public class Trail {
    private TrailType type;

    private Object id;

    private Object var1, var2; //generic variables *(type depends on trailtype)*

    private int tickDelay = 1;

    private DyeColor color1, color2, color3;

    public Trail(EnumParticle p){
        type = TrailType.Particle;
        id = p;
    }

    public Trail(Material m){
        this(m,EnumParticle.CLOUD);
    }

    public Trail(Material m, EnumParticle p){
        type = TrailType.Rain;
        id = m;

        var1 = p; //The type of cloud (CLOUD or SMOKE_LARGE usually)

        tickDelay = 2;
        var2 = 3; //itemstack spawn delay in tick/2 (5 delay = 10 ingame ticks)
    }

    public Trail(WingType wt, DyeColor inner, DyeColor outer, DyeColor edge){
        type = TrailType.Wings;

        id = wt;

        color1 = inner;
        color2 = outer;
        color3 = edge;

        tickDelay = 2;
    }

    public void draw(Entity e, int tick){

        if(type == TrailType.Particle){
            if(tick % tickDelay > 0) return;

            Location eLocation = e.getLocation();
            eLocation.add(0,e.getHeight()/2,0);

            float randseed = 0f;

            //if data == 1 on redstone, note, or mobspell(ambient too), the color is randomized as long as count > 0
            float data =
                    id==EnumParticle.REDSTONE || id==EnumParticle.NOTE ||
                    id==EnumParticle.SPELL_MOB || id==EnumParticle.SPELL_MOB_AMBIENT
                    ? 1f : 0f;

            sendParticlePacket((EnumParticle)id,eLocation,randseed,randseed,randseed,data,1);

        }else if(type == TrailType.Rain){
            Location eLocation = e.getLocation();


            if(tick % tickDelay == 0) {
                Location cloudPos = eLocation.clone().add(0,e.getHeight()*2f,0);
                sendParticlePacket((EnumParticle)var1, cloudPos, 0.3f, 0.1f, 0.3f, 0.0001f, 5);

                Location itemPos = eLocation.clone().add(0,e.getHeight()*2f,0);

                ItemStack is = new ItemStack((Material)id,1);
                ItemMeta im = is.getItemMeta();
                im.setDisplayName("§r§0RAIN_ITEM_NPU");
                is.setItemMeta(im);

                if(Arrays.asList(PLSTrails.shinyRainTypes).indexOf(id)>=0){
                    is = Utils.shiny(is);
                }

                Item i = e.getWorld().dropItemNaturally(itemPos,is);
                i.setTicksLived(12000);
                i.setPickupDelay(Integer.MAX_VALUE);
                i.setVelocity(i.getVelocity().setY(-0.3f));
                PLSTrails.instance.rainItems.add(i);
            }
        }else if(type == TrailType.Wings){
            if(tick % tickDelay > 0) return;

            Location eLocation = e.getLocation();

            Color c1 = color1.getColor();
            Color c2 = color2.getColor();
            Color c3 = color3.getColor();

            float wingpos = 75;
            int movespeed = 2;

            int ct = tick * movespeed;
            if(ct > 100) ct -= 100;

            if( ct < 50 )
                wingpos -= ct;
            else
                wingpos -= 100 - ct;

            System.out.println("Drawing wings for " + e.getName() + " at tick " + tick + " wp = " + wingpos);

            drawWings((WingType)id, eLocation, wingpos, c1, c2, c3);
        }
    }

    void drawWings(WingType type, Location eLocation, float wingPos, Color c1, Color c2, Color c3){
        if(type == WingType.Butterfly) {
            for (int side = 0; side <= 1; side++) {
                Location eLocation1 = eLocation.clone();
                eLocation1.setPitch(0.0F);
                eLocation1.subtract(eLocation1.getDirection().multiply(0.2f));
                eLocation1.setYaw(eLocation1.getYaw() + (side == 0 ? wingPos : -wingPos));
                Location eLocation2 = eLocation1.clone();

                sendColoredRedstone(eLocation2.clone().subtract(eLocation2.getDirection().multiply(0.8f)), c1);
                sendColoredRedstone(eLocation2.clone().subtract(eLocation2.getDirection().multiply(1.0f)), c1);
                eLocation1.add(0, 0.2, 0);
                eLocation2 = eLocation1.clone();
                sendColoredRedstone(eLocation2.clone().subtract(eLocation2.getDirection().multiply(0.6f)), c1);
                sendColoredRedstone(eLocation2.clone().subtract(eLocation2.getDirection().multiply(0.8f)), c1);
                sendColoredRedstone(eLocation2.clone().subtract(eLocation2.getDirection().multiply(1.0f)), c2);
                sendColoredRedstone(eLocation2.clone().subtract(eLocation2.getDirection().multiply(1.2f)), c2);
                eLocation1.add(0, 0.2, 0);
                eLocation2 = eLocation1.clone();
                sendColoredRedstone(eLocation2.clone().subtract(eLocation2.getDirection().multiply(0.4f)), c1);
                sendColoredRedstone(eLocation2.clone().subtract(eLocation2.getDirection().multiply(0.6f)), c1);
                sendColoredRedstone(eLocation2.clone().subtract(eLocation2.getDirection().multiply(0.8f)), c2);
                sendColoredRedstone(eLocation2.clone().subtract(eLocation2.getDirection().multiply(1.0f)), c2);
                sendColoredRedstone(eLocation2.clone().subtract(eLocation2.getDirection().multiply(1.2f)), c2);

                for (int l = 0; l < 6; l++) {
                    double doff = l == 1 || l == 2 ? 0.1 : 0;

                    eLocation1.add(0, 0.2, 0);
                    eLocation2 = eLocation1.clone();
                    sendColoredRedstone(eLocation2.clone().subtract(eLocation2.getDirection().multiply(0.2D - doff)), c1);
                    sendColoredRedstone(eLocation2.clone().subtract(eLocation2.getDirection().multiply(0.4D - doff)), c1);
                    sendColoredRedstone(eLocation2.clone().subtract(eLocation2.getDirection().multiply(0.6D - doff)), c2);
                    sendColoredRedstone(eLocation2.clone().subtract(eLocation2.getDirection().multiply(0.8D - doff)), c2);
                    sendColoredRedstone(eLocation2.clone().subtract(eLocation2.getDirection().multiply(1.0D - doff)), c2);
                    sendColoredRedstone(eLocation2.clone().subtract(eLocation2.getDirection().multiply(1.2D - doff)), c3);
                    sendColoredRedstone(eLocation2.clone().subtract(eLocation2.getDirection().multiply(1.4D - doff)), c3);
                    sendColoredRedstone(eLocation2.clone().subtract(eLocation2.getDirection().multiply(1.6D - doff)), c3);
                }
                eLocation1.add(0.0D, 0.2D, 0.0D);
                eLocation2 = eLocation1.clone();
                sendColoredRedstone(eLocation2.clone().subtract(eLocation2.getDirection().multiply(0.6D)), c1);
                sendColoredRedstone(eLocation2.clone().subtract(eLocation2.getDirection().multiply(0.8D)), c2);
                sendColoredRedstone(eLocation2.clone().subtract(eLocation2.getDirection().multiply(1.0D)), c2);
                sendColoredRedstone(eLocation2.clone().subtract(eLocation2.getDirection().multiply(1.2D)), c2);
                sendColoredRedstone(eLocation2.clone().subtract(eLocation2.getDirection().multiply(1.4D)), c2);
                sendColoredRedstone(eLocation2.clone().subtract(eLocation2.getDirection().multiply(1.6D)), c3);
                sendColoredRedstone(eLocation2.clone().subtract(eLocation2.getDirection().multiply(1.8D)), c3);
                eLocation1.add(0.0D, 0.2D, 0.0D);
                eLocation2 = eLocation1.clone();
                sendColoredRedstone(eLocation2.clone().subtract(eLocation2.getDirection().multiply(1.0D)), c2);
                sendColoredRedstone(eLocation2.clone().subtract(eLocation2.getDirection().multiply(1.2D)), c2);
                sendColoredRedstone(eLocation2.clone().subtract(eLocation2.getDirection().multiply(1.4D)), c2);
                sendColoredRedstone(eLocation2.clone().subtract(eLocation2.getDirection().multiply(1.6D)), c3);
                sendColoredRedstone(eLocation2.clone().subtract(eLocation2.getDirection().multiply(1.8D)), c3);
            }
        }
    }

    public void sendColoredRedstone(Location l, Color c){

        float r = c.getRed()/255f,g = c.getGreen()/255f,b = c.getBlue()/255f;
        if(r == 0f) r = 0.001f;
        sendParticlePacket(EnumParticle.REDSTONE, l.getWorld(), (float)l.getX(), (float)l.getY(), (float)l.getZ(), r, g, b, 1f, 0);
    }

    public void sendParticlePacket(EnumParticle id, Location l, float d1, float d2, float d3, float data, int count) {
        sendParticlePacket(id,l.getWorld(),(float)l.getX(),(float)l.getY(),(float)l.getZ(),d1,d2,d3,data,count);
    }
    public void sendParticlePacket(EnumParticle id, World w, float x, float y, float z, float d1, float d2, float d3, float data, int count){
        final PacketPlayOutWorldParticles pkt = new PacketPlayOutWorldParticles(
                id,true,
                x,y,z,
                d1,d2,d3,
                data,count, (int[]) null);

        final Location prtLocation = new Location(w,x,y,z);

        Bukkit.getOnlinePlayers().forEach((Consumer<Player>) player -> {
            if(player.getLocation().distance(prtLocation) < 45){
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(pkt);
            }
        });
    }
}
