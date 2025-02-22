package ru.glkot.sexIon.menus;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;


public class AdminMenu {

    public static ItemStack IT (Material material, String name, String action, String args) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(action));
        lore.add(Component.text(args));
        meta.displayName(Component.text(name));
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static Inventory open() {
        List<ItemStack> items = new ArrayList<>();
        items.add(IT(Material.POPPED_CHORUS_FRUIT,"SEX-IONS","open-list","sexion"));
        items.add(IT(Material.SHULKER_SHELL,"Models","open-list","models"));
        items.add(IT(Material.END_CRYSTAL,"Buttons","open-list","buttons"));
        items.add(IT(Material.BLUE_DYE,"Blueprints","open-list","blueprints"));
        Scrollin scrollin = new Scrollin(items);
        return scrollin.page(0);
    }
}
