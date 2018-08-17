package today.pls.trails;

import org.bukkit.Material;

public enum WingType {
    Butterfly(Material.DOUBLE_PLANT);

    Material menuMat;

    WingType(Material m) {
        this.menuMat = m;
    }

    public static WingType getByMaterial(Material m) {
        if(m == null) return null;
        
        for (WingType wt: WingType.values()) {
            if(wt.menuMat == m) return wt;
        }

        return null;
    }
}
