package ru.glkot.sexIon.menus;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Buttons {
    public static ItemStack blank() {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.setItemModel(new NamespacedKey("general","button/nothing"));
        meta.setHideTooltip(true);
        item.setItemMeta(meta);
        return item;
    }
    static ItemStack left() {
        ItemStack item = new ItemStack(Material.BONE_MEAL);
        ItemMeta meta = item.getItemMeta();
        meta.setItemModel(new NamespacedKey("general","button/arrow/left"));
        meta.setHideTooltip(true);
        item.setItemMeta(meta);
        return item;
    }
    static ItemStack right() {
        ItemStack item = new ItemStack(Material.BONE_MEAL);
        ItemMeta meta = item.getItemMeta();
        meta.setItemModel(new NamespacedKey("general","button/arrow/right"));
        meta.setHideTooltip(true);
        item.setItemMeta(meta);
        return item;
    }
    static ItemStack exit(int id, String name) {
        ItemStack item = new ItemStack(Material.REDSTONE);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(name));
        meta.setItemModel(new NamespacedKey("general","button/quit"));
        meta.setHideTooltip(true);
        item.setItemMeta(meta);
        item.setAmount(id+1);
        return item;
    }

}
