package ru.glkot.sexIon;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import ru.glkot.sexIon.menus.AdminMenu;
import ru.glkot.sexIon.menus.Scrollin;
import ru.glkot.sexIon.playerdata.PlayerData;

import java.util.*;

public class CommandListener {

    public static Component sxOutText(String s) {
        Random random = new Random();
        return Component.text("=== " + s + " ===").color(TextColor.color(random.nextInt(140, 180), random.nextInt(50, 90), random.nextInt(130, 160)));

        //163, 77, 148
    }


    static void nahuy(JavaPlugin plugin) {
        {
            plugin.getCommand("sex-ion").setExecutor(new CommandExecutor() {
                @Override
                public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

                    switch (strings.length) {
                        case 1 -> {
                            switch (strings[0]) {
                                case "new" -> {

                                    UUID uuid = UUID.randomUUID();


                                    commandSender.sendMessage("\n");
                                    commandSender.sendMessage(sxOutText("Created new session"));
                                    commandSender.sendMessage(sxOutText("COPY ID BY CLICK").clickEvent(ClickEvent.copyToClipboard(uuid.toString())));
                                    commandSender.sendMessage("\n");

                                    SexIon sexIon = new SexIon(uuid);

                                }

                            }
                            return false;
                        }
                        case 2 -> {
                            switch (strings[0]) {
                                case "kill" -> {

                                    UUID uuid = UUID.fromString(strings[1]);

                                    commandSender.sendMessage("\n");
                                    commandSender.sendMessage(sxOutText("Killed session"));
                                    commandSender.sendMessage(sxOutText("COPY ID BY CLICK").clickEvent(ClickEvent.copyToClipboard(uuid.toString())));
                                    commandSender.sendMessage("\n");

                                    SexIon sexIon = SexIon.get(uuid);
                                    sexIon.kill();
                                    return false;
                                }

                                case "get" -> {

                                    UUID uuid = UUID.fromString(strings[1]);

                                    commandSender.sendMessage("\n");
                                    commandSender.sendMessage(sxOutText("Got session"));
                                    commandSender.sendMessage(sxOutText("COPY ID BY CLICK").clickEvent(ClickEvent.copyToClipboard(uuid.toString())));
                                    commandSender.sendMessage("\n");

                                    SexIon sexIon = SexIon.get(uuid);
                                    return false;
                                }

                                case "generate" -> {

                                    UUID uuid = UUID.fromString(strings[1]);

                                    SexIon sexIon = SexIon.get(uuid);
                                    if (sexIon.generated()) {

                                        commandSender.sendMessage("\n");
                                        commandSender.sendMessage(sxOutText("Session is already generated"));
                                        commandSender.sendMessage(sxOutText("COPY ID BY CLICK").clickEvent(ClickEvent.copyToClipboard(uuid.toString())));
                                        commandSender.sendMessage("\n");


                                    } else {
                                        commandSender.sendMessage("\n");
                                        commandSender.sendMessage(sxOutText("Gen session"));
                                        commandSender.sendMessage(sxOutText("COPY ID BY CLICK").clickEvent(ClickEvent.copyToClipboard(uuid.toString())));
                                        commandSender.sendMessage("\n");

                                        sexIon.generate();
                                    }
                                    return false;
                                }

                                case "teleport" -> {

                                    UUID uuid = UUID.fromString(strings[1]);

                                    SexIon sexIon = SexIon.get(uuid);
                                    if (sexIon.generated()) {

                                        commandSender.sendMessage("\n");
                                        commandSender.sendMessage(sxOutText("Teleporting to session"));
                                        commandSender.sendMessage(sxOutText("COPY ID BY CLICK").clickEvent(ClickEvent.copyToClipboard(uuid.toString())));
                                        commandSender.sendMessage("\n");

                                        ((Player) commandSender).teleport(new Location(sexIon.world(), 0, 100, 0));

                                    } else {
                                        commandSender.sendMessage("\n");
                                        commandSender.sendMessage(sxOutText("Session is not generated now"));
                                        commandSender.sendMessage(sxOutText("COPY ID BY CLICK").clickEvent(ClickEvent.copyToClipboard(uuid.toString())));
                                        commandSender.sendMessage("\n");

                                    }
                                    return false;
                                }
                            }
                        }
                    }

                    return true;
                }
            });


            plugin.getCommand("sex-ion").setTabCompleter(new TabCompleter() {
                @Override
                public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

                    List<String> output = new java.util.ArrayList<>();


                    switch (strings.length) {
                        case 1 -> {
                            output.add("new");
                            output.add("get");
                            output.add("kill");
                            output.add("generate");
                            output.add("teleport");
                        }
                        case 2 -> {
                            switch (strings[0]) {
                                case "get", "kill" -> {
                                    for (UUID uuid : SexIon.getAllIds()) {
                                        output.add(uuid.toString());
                                    }
                                }

                                case "generate" -> {
                                    for (SexIon sexIon : SexIon.getAll()) {
                                        if (!sexIon.generated()) {
                                            output.add(sexIon.id.toString());
                                        }
                                    }
                                }

                                case "teleport" -> {
                                    for (SexIon sexIon : SexIon.getAll()) {
                                        if (sexIon.generated()) {
                                            output.add(sexIon.id.toString());
                                        }
                                    }
                                }
                            }
                        }
                    }


                    return output;
                }
            });

        }
        plugin.getCommand("blueprint").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
                Player player = (Player) commandSender;
                Location location = player.getTargetBlockExact(100).getLocation();
                player.sendMessage(location.toString());
                Blueprint blueprint = new Blueprint(location.clone().add(2,1,2),location.clone().add(-2,5,-2));
                blueprint.redactor(player);
                Sxlib.get().blueprintMap.put(blueprint.getId().toString(),blueprint);
                return false;
            }
        });

        plugin.getCommand("test-menu").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
                Player player = (Player) commandSender;

                List<ItemStack> list = new ArrayList<>();
                for (int i = 0; i < Integer.parseInt(strings[0]); i++ ){

                    ItemStack item = new ItemStack(Material.PAPER);
                    ItemMeta meta = item.getItemMeta();
                    meta.displayName(Component.text(i+1));
                    item.setItemMeta(meta);
                    list.add(item);
                }
                player.openInventory(new Scrollin(list).page(0));

                return false;
            }
        });

        plugin.getCommand("menu").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

                ((Player) commandSender).openInventory(AdminMenu.open());

                return false;
            }
        });

        plugin.getCommand("playerdata").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
                if (strings.length > 0) {
                    switch (strings[0]) {
                        case "get" -> {
                            if (strings.length == 2) {
                                PlayerData data = new PlayerData(((Player) commandSender).getUniqueId());
                                if (!data.data.keySet().contains(strings[1])) return true;
                                commandSender.sendMessage(Component.text(data.getString(strings[1])));
                                return false;
                            }
                        }
                        case "add" -> {
                            if (strings.length == 3) {
                                PlayerData data = new PlayerData(((Player) commandSender).getUniqueId());
                                data.put(strings[1],strings[2]);

                                return false;
                            }
                        }
                    }
                }
                return true;
            }
        });

        plugin.getCommand("playerdata").setTabCompleter(new TabCompleter() {
            @Override
            public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
                List<String> output = new ArrayList<>();
                switch (strings.length) {
                    case 1 -> {
                        output.add("add");
                        output.add("get");
                    }
                    case 2 -> {
                        output.addAll(new PlayerData(((Player) commandSender).getUniqueId()).data.keySet());
                    }
                }
                commandSender.sendMessage(Component.text(strings.length));


                return output;
            }
        });

        plugin.getCommand("guibychar").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

                // Правильное экранирование Unicode
                JSONObject jsonObject = new JSONObject(strings[0]);


                Component component = Component.translatable("space.-8").font(Key.key("space:default")).append(Component.text(jsonObject.getString("char")).font(Key.key("default")).color(TextColor.color(255,255,255)));

                // Создание инвентаря с обработанным названием
                Inventory inventory = Bukkit.createInventory(null, 54, component);

                // Открытие инвентаря для игрока
                ((Player) commandSender).openInventory(inventory);

                return false;
            }
        });

        plugin.getCommand("spawnprop").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {

                Player player = (Player) commandSender;

                Prop prop = Prop.spawn(player.getLocation());

                prop.show(player);

                return false;
            }
        });

        plugin.getCommand("outline").setExecutor(new CommandExecutor() {
            @Override
            public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {

                Player player = (Player) commandSender;
                Location playerLoc = player.getLocation();
                Location viewPoint = player.getTargetBlockExact(1000).getLocation().add(player.getTargetBlockFace(1000).getDirection());

                int minX = Integer.min(playerLoc.getBlockX(),viewPoint.getBlockX());
                int maxX = Integer.max(playerLoc.getBlockX(),viewPoint.getBlockX());
                int minY = Integer.min(playerLoc.getBlockY(),viewPoint.getBlockY());
                int maxY = Integer.max(playerLoc.getBlockY(),viewPoint.getBlockY());
                int minZ = Integer.min(playerLoc.getBlockZ(),viewPoint.getBlockZ());
                int maxZ = Integer.max(playerLoc.getBlockZ(),viewPoint.getBlockZ());

                new Thread(() -> {

                    Outline outline = new Outline(minX,minY,minZ,maxX,maxY,maxZ,playerLoc.getWorld(),UUID.randomUUID());
                    outline.spawn();

                    while (player.isConnected()) {
                        if (player.isSneaking()) {


                            Location playerLoc1 = player.getLocation();
                            Location viewPoint1 = player.getTargetBlockExact(1000).getLocation().add(player.getTargetBlockFace(1000).getDirection());

                            int minxX = Integer.min(playerLoc1.getBlockX(),viewPoint1.getBlockX());
                            int maxxX = Integer.max(playerLoc1.getBlockX(),viewPoint1.getBlockX());
                            int minxY = Integer.min(playerLoc1.getBlockY(),viewPoint1.getBlockY());
                            int maxxY = Integer.max(playerLoc1.getBlockY(),viewPoint1.getBlockY());
                            int minxZ = Integer.min(playerLoc1.getBlockZ(),viewPoint1.getBlockZ());
                            int maxxZ = Integer.max(playerLoc1.getBlockZ(),viewPoint1.getBlockZ());



                            outline.resize(minxX,minxY,minxZ,maxxX,maxxY,maxxZ);
                        }

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    outline.deleteAll();

                }).start();

                return false;
            }
        });

    }
}
