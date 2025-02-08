package ru.glkot.sexIon;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class StringGetter {

    private Runnable runnable;
    private Player player;
    public StringGetter (Runnable run, Player player) {
        this.runnable = run;
        this.player = player;
        flush();
    }

    public void flush () {
        Sxlib.get().stringGetterMap.put(player.getName(),this);
    }

    public void open() {
        Inventory inventory = Bukkit.createInventory(null, InventoryType.ANVIL);
        ItemStack stack = new ItemStack(Material.NAME_TAG);
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(Component.text("Name blueprint"));
        meta.setHideTooltip(true);
        stack.setItemMeta(meta);

        inventory.setItem(0,stack);

        stack.setItemMeta(meta);
        inventory.setItem(2,stack);
        player.openInventory(inventory);
    }

    public void run() {
        runnable.run();
    }


}
