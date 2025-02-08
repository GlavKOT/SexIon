package ru.glkot.sexIon;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class Prop {

    private JavaPlugin plugin = JavaPlugin.getPlugin(Sxlib.class);
    private ItemDisplay display;
    private boolean physics;
    private Vector velocity;
    private Location location() {
        return display.getLocation();
    }


    private Prop(ItemDisplay itemDisplay ) {
        display = itemDisplay;
        physics = true;
        velocity = new Vector(0,0,0);
        display.setItemStack(new ItemStack(Material.ACACIA_WOOD));
        display.setVisibleByDefault(false);
    }



    public static Prop spawn(Location location) {
        return new Prop(location.getWorld().spawn(location, ItemDisplay.class));
    }

    public void show(Player player) {
        player.showEntity(plugin, display);
    }

    public void setItemStack(ItemStack itemStack) {
        display.setItemStack(itemStack);
    }

    public void setModel(String model) {
        ItemStack itemStack = display.getItemStack();
        ItemMeta meta = itemStack.getItemMeta();
        meta.setItemModel(new NamespacedKey("general",model));
        itemStack.setItemMeta(meta);
        display.setItemStack(itemStack);
    }

    public void setPhysics(boolean b) {
        physics = b;
    }

}