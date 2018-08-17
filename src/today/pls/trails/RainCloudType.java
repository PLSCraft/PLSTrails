package today.pls.trails;

import net.minecraft.server.v1_12_R1.EnumParticle;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Colorable;
import org.bukkit.material.Wool;

public enum RainCloudType {
    White("§b§lWhite Cloud",DyeColor.WHITE, EnumParticle.CLOUD),
    Black("§b§lBlack Cloud",DyeColor.BLACK, EnumParticle.SMOKE_LARGE);


    public DyeColor color;
    public EnumParticle particle;
    public String name;

    RainCloudType(String n, DyeColor c, EnumParticle p) {
        this.name = n;
        this.color = c;
        this.particle = p;
    }

    public ItemStack getAsWool(){
        return new ItemStack(Material.WOOL,1,color.getWoolData());
    }

    public static RainCloudType getByWool(Wool w) {
        for (RainCloudType rct: RainCloudType.values()) {
            if(rct.color == w.getColor())
                return rct;
        }

        return null;
    }
}
