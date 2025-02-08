package ru.glkot.sexIon.menus;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static ru.glkot.sexIon.menus.Buttons.*;

public class Scrollin {

    private static Map<String,Scrollin> scrollinMap;
    public final List<ItemStack> items;
    public final String id;
    public final int max;

    public Scrollin (List<ItemStack> items) {
        if (scrollinMap == null) scrollinMap = new HashMap<>();
        this.items = items;
        this.id = UUID.randomUUID().toString();
        int mx = items.size() / 36;
        double temp = items.size();
        if ((temp / 36) - mx > 0) this.max = mx + 1;
        else this.max = mx;
        scrollinMap.put(id,this);
        System.err.println(scrollinMap);
    }
    public static Scrollin get(String id) {
        return scrollinMap.get(id);
    }

    public Inventory page (int id) {
        Component component = Component.translatable("space.-8").font(Key.key("space:default")).append(Component.text('\uE132').font(Key.key("default")).color(TextColor.color(255,255,255)));
        Inventory inv = Bukkit.createInventory(null,54, component);


        inv.setItem(0,exit(id,this.id));
        inv.setItem(1,blank());
        inv.setItem(2,blank());
        inv.setItem(3,blank());
        inv.setItem(4,blank());
        inv.setItem(5,blank());
        inv.setItem(6,blank());
        inv.setItem(7,blank());
        inv.setItem(8,blank());

        inv.setItem(53,blank());
        inv.setItem(52,blank());
        inv.setItem(51,blank());
        inv.setItem(50,right());
        inv.setItem(49,blank());
        inv.setItem(48,left());
        inv.setItem(47,blank());
        inv.setItem(46,blank());
        inv.setItem(45,blank());

        for (int i = 0;i<36;i++) {
            if ((id * 36 + i)>=items.size()) break;
            ItemStack item = items.get(id * 36 + i);
            inv.setItem(i + 9, item);
        }



        return inv;
    }
}
