package ru.glkot.sexIon.menus;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.units.qual.N;

import java.util.ArrayList;
import java.util.List;

public class Buttons {
    static ItemStack blank () {
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
    static ItemStack exit(int id, String base64, String action, int max) {
        ItemStack item = new ItemStack(Material.REDSTONE);
        ItemMeta meta = item.getItemMeta();
        meta.setItemModel(new NamespacedKey("general","button/quit"));
        meta.setHideTooltip(true);
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(id));
        lore.add(Component.text(base64));
        lore.add(Component.text(action));
        lore.add(Component.text(max));
        meta.lore(lore);
        item.setItemMeta(meta);
        item.setAmount(id+1);
        return item;
    }

}
