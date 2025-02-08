package ru.glkot.sexIon.menus;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.units.qual.N;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import static ru.glkot.sexIon.menus.Buttons.*;

public class Scrollin {


    private final List<ItemStack> items;
    private final String id;
    private final String action;
    private final int max;

    public Scrollin (List<ItemStack> items, String action) {
        this.items = items;
        this.id = UUID.randomUUID().toString();
        this.action = action;
        int mx = items.size() / 36;
        double temp = items.size();
        if ((temp / 36) - mx > 0) this.max = mx + 1;
        else this.max = mx;
        try {
            Bukkit.getWorld("world").getPersistentDataContainer().set(new NamespacedKey("huy",id), PersistentDataType.STRING, encodeItemStackListToBase64(items));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public Scrollin (String id, String action) {
        try {
            this.items = decodeItemStackListFromBase64(Bukkit.getWorld("world").getPersistentDataContainer().get(new NamespacedKey("huy",id),PersistentDataType.STRING));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.id = id;
        this.action = action;
        int mx = items.size() / 36;
        double temp = items.size();
        if ((temp / 36) - mx > 0) this.max = mx + 1;
        else this.max = mx;
    }

    public Inventory page (int id) {
        Component component = Component.translatable("space.-8").font(Key.key("space:default")).append(Component.text('\uE132').font(Key.key("default")).color(TextColor.color(255,255,255)));
        Inventory inv = Bukkit.createInventory(null,54, component);


        inv.setItem(0,exit(id,this.id, action,max));
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
    // Кодируем список ItemStack в Base64
    public static String encodeItemStackListToBase64(List<ItemStack> items) throws IOException {
        // Создаем YAML-конфигурацию
        YamlConfiguration yaml = new YamlConfiguration();

        // Сохраняем каждый ItemStack в конфигурацию
        for (int i = 0; i < items.size(); i++) {
            yaml.set("item" + i, items.get(i));
        }

        // Преобразуем YAML в строку

        // Кодируем YAML-строку в Base64
        return yaml.saveToString();
    }

    // Декодируем список ItemStack из Base64
    public static List<ItemStack> decodeItemStackListFromBase64(String base64) throws IOException {
        // Загружаем YAML-конфигурацию
        YamlConfiguration yaml = new YamlConfiguration();
        try {
            yaml.loadFromString(base64);
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }

        // Восстанавливаем список ItemStack
        List<ItemStack> items = new java.util.ArrayList<>();
        for (String key : yaml.getKeys(false)) {
            items.add(yaml.getItemStack(key));
        }

        return items;
    }
}
