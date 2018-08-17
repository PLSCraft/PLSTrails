package today.pls.trails;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class Utils {

    public static ItemStack itemStackWithName(Material m, String n){
        return itemStackWithNameAndLore(m,n,null);
    }

    public static ItemStack itemStackWithNameAndLore(Material m, String n, String l){
        return setItemNameAndLore(new ItemStack(m),n,l);
    }

    public static ItemStack setItemNameAndLore(ItemStack i, String n, String l){
        ItemMeta m = i.getItemMeta();
        if(l != null){
            m.setLore(Arrays.asList(l.split("\n")));
        }
        m.setDisplayName(n);
        i.setItemMeta(m);
        return i;
    }

    public static ItemStack shiny(ItemStack is){
        ItemMeta im = is.getItemMeta();
        im.addEnchant(Enchantment.THORNS,1337,true);
        im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        is.setItemMeta(im);

        return is;
    }

    public static ItemStack itemStackWithNameShiny(Material m, String s) {
        return shiny(itemStackWithName(m,s));
    }
}
