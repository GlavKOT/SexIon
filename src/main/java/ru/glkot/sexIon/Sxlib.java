package ru.glkot.sexIon;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.glkot.sexIon.menus.*;
import ru.glkot.sexIon.messaging.Receiver;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public final class Sxlib extends JavaPlugin {

    public UUID id;
    public JSONObject config;
    public Map<String, BlueprintBuilder> blueprintMap;
    public Map<String, Map<Integer, ItemStack>> oldHotBars;
    public List<String> titleTickers;
    private static Sxlib instance;


    public static JSONObject getCon() {
        File confile = Path.of(Path.of("").toAbsolutePath().toString(), "sex-config.json").toFile();
        try (FileInputStream fis = new FileInputStream(confile)) {
            return new JSONObject(new String(fis.readAllBytes()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        blueprintMap = new HashMap<>();
        oldHotBars = new HashMap<>();
        titleTickers = new ArrayList<>();

        File confile = Path.of(Path.of("").toAbsolutePath().toString(), "sex-config.json").toFile();
        if (confile.exists()) {
            try (FileInputStream fis = new FileInputStream(confile)) {
                config = new JSONObject(new String(fis.readAllBytes()));
                if (!config.keySet().contains("server-type")) config.put("server-type", "test");
                if (!config.keySet().contains("max-sex")) config.put("max-sex", "1");
                if (!config.keySet().contains("veslo-ip")) config.put("veslo-ip", "0.0.0.0");
                if (!config.keySet().contains("sex-settings")) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("default-max-time", 600);
                    config.put("sex-setting", jsonObject);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            config = new JSONObject();
            config.put("server-type", "test");
            config.put("max-sex", "1");
            config.put("veslo-ip", "0.0.0.0");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("default-max-time", 600);
            config.put("sex-setting", jsonObject);
        }
        try (Writer writer = new FileWriter(confile)) {
            writer.write(config.toString(4));
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Bukkit.getServer().sendMessage(Component.text("===== SEX-LIB LOADED =====").color(TextColor.color(163, 77, 148)));
        Bukkit.getPluginManager().registerEvents(new ScrollinListener(), this);
        Bukkit.getPluginManager().registerEvents(new MainListener(), this);
        CommandListener.nahuy(this);
        Path.of(Path.of("").toAbsolutePath().toString(), "PlayerData").toFile().mkdir();
        Receiver.startServer(this);
        id = UUID.randomUUID();
        config.put("server-id", id);
        try {
            Writer writer = new FileWriter(confile);
            writer.write(config.toString(4));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        genId();
        Receiver.receiveData("log", "HUY");

    }

    public void genId() {
        Receiver.receiveID("id-add", getCon().getString("server-id"), config.getString("server-type"));
    }

    public static Sxlib get() {
        return instance;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        for (BlueprintBuilder blueprintBuilder : blueprintMap.values()) {
            for (UUID itemDisplay : blueprintBuilder.outline.getFaces().values()) {
                Bukkit.getEntity(itemDisplay).remove();
            }
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.removeScoreboardTag("bluered." + blueprintBuilder.getId());
            }
        }

        for (String s : titleTickers) {

            Player player = Bukkit.getPlayer(s);

            if (player != null) {
                for (int i = 0; i <= 8; i++) {
                    Map<Integer, ItemStack> stackMap = oldHotBars.get(s);
                    player.getInventory().setItem(i, stackMap.get(i));
                }
            }

        }

        Bukkit.getServer().sendMessage(Component.text("===== SEX-LIB UNLOADED =====").color(TextColor.color(102, 0, 163)));
        for (NamespacedKey key : Bukkit.getWorld("world").getPersistentDataContainer().getKeys()) {
            if (key.getNamespace().equals("huy")) {
                Bukkit.getWorld("world").getPersistentDataContainer().remove(key);
            }
        }

        Receiver.receiveID("id-remove", getCon().getString("server-id"), config.getString("server-type"));


    }
}
