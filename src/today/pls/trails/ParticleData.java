package today.pls.trails;

import net.minecraft.server.v1_12_R1.EnumParticle;
import org.bukkit.Material;
import org.bukkit.Particle;

public enum ParticleData {
    SPLASH(Material.WATER_LILY,"Water Splash",EnumParticle.WATER_SPLASH),
    CRIT(Material.DIAMOND_SWORD,"Crit",EnumParticle.CRIT),
    SPELL(Material.SPLASH_POTION,"Spell",EnumParticle.SPELL),
    DRIP_WATER(Material.WATER_BUCKET,"Water Drip",EnumParticle.DRIP_WATER),
    DRIP_LAVA(Material.LAVA_BUCKET,"Lava Drip",EnumParticle.DRIP_LAVA),
    ANGRY(Material.WOOD_SWORD,"Angry",EnumParticle.VILLAGER_ANGRY),
    NOTE(Material.NOTE_BLOCK,"Note",EnumParticle.NOTE),
    PORTAL(Material.OBSIDIAN,"Portal",EnumParticle.PORTAL),
    FIRE(Material.FIREBALL,"Flame",EnumParticle.FLAME),
    CLOUD(Material.WOOL,"Cloud",EnumParticle.CLOUD),
    REDSTONE(Material.REDSTONE,"Redstone",EnumParticle.REDSTONE),
    SNOW(Material.SNOW_BALL,"Snowball",EnumParticle.SNOWBALL),
    SLIME(Material.SLIME_BALL,"Slime",EnumParticle.SLIME),
    HEART(Material.WHEAT,"Heart",EnumParticle.HEART),
    END_ROD(Material.END_ROD,"End Rod",EnumParticle.END_ROD),
    DAMAGE_INDICATOR(Material.STONE_SWORD,"Damage Indicator",EnumParticle.DAMAGE_INDICATOR);

    public Material menuMaterial;
    public String particleName;
    public EnumParticle particle;

    ParticleData(Material m, String n, EnumParticle p){
        menuMaterial = m;
        particleName = n;
        particle = p;
    }

    public static ParticleData getDataFromMaterial(Material m) {

        for (ParticleData pd: ParticleData.values()) {
            if(m == pd.menuMaterial){
                return pd;
            }
        }
        return null;
    }
}
