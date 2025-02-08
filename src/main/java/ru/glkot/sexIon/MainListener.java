package ru.glkot.sexIon;

import com.destroystokyo.paper.ParticleBuilder;
import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import ru.glkot.sexIon.menus.Buttons;
import ru.glkot.sexIon.playerdata.PlayerData;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public class MainListener implements Listener {

    @EventHandler
    public void huy(PlayerJoinEvent e) {
        new PlayerData(e.getPlayer().getUniqueId());

        // Sending RP
        try {

            FileInputStream zov = new FileInputStream(Path.of(Path.of("").toAbsolutePath().toString(), "rp.hash").toFile());

            ResourcePackRequest.@NotNull Builder resourcePackRequest = ResourcePackRequest.resourcePackRequest();
            ResourcePackInfo resourcePackInfo = ResourcePackInfo.resourcePackInfo(UUID.fromString("b102e59c-9018-303a-8fb7-5079edb6cb63"), URI.create("http://103.137.251.165:8080/RP.zip"), new String(zov.readAllBytes()));

            resourcePackRequest.packs(resourcePackInfo);

            e.getPlayer().sendResourcePacks(resourcePackRequest.asResourcePackRequest());

        } catch (IOException ev) {
            throw new RuntimeException(ev);
        }
    }

    @EventHandler
    public void TickerTitleBlueprint(ServerTickStartEvent event) {
        for (String s : Sxlib.get().titleTickers) {
            Player player = Bukkit.getPlayerExact(s);
            if (player != null) {
                player.showTitle(Title.title(Component.text("\uE020\uE036").font(NamespacedKey.fromString("minecraft:default"))
                                .append(Component.translatable("space.100").font(NamespacedKey.fromString("space:default")))
                                .append(Component.text("\uE09E\uE03C").font(NamespacedKey.fromString("minecraft:default")))
                        , Component.text(""), Title.Times.times(Duration.ZERO, Duration.ofSeconds(1), Duration.ZERO)));

            }


        }
    }

//    @EventHandler
//    public void Getter(PrepareAnvilEvent e) {
//
//        AnvilInventory inventory = e.getInventory();
//        HumanEntity humanEntity = e.getView().getPlayer();
//        humanEntity.sendMessage("efaw");
//        if (Sxlib.get().titleTickers.contains(humanEntity.getName())) {
//
//            ItemStack stack = new ItemStack(Material.NAME_TAG);
//            ItemMeta meta = stack.getItemMeta();
//            meta.setHideTooltip(true);
//            stack.setItemMeta(meta);
//
//            inventory.setFirstItem(stack);
//
//            meta = inventory.getResult().getItemMeta();
//            stack.setItemMeta(meta);
//            inventory.setResult(stack);
//            humanEntity.closeInventory();
//            humanEntity.openInventory(inventory);
//        }
//
//    }
//
//
//    @EventHandler
//    public void GetterR(InventoryClickEvent e) {
//        StringGetter stringGetter = Sxlib.get().stringGetterMap.get(e.getWhoClicked().getName());
//        if (stringGetter != null) {
//            if (Sxlib.get().titleTickers.contains(e.getWhoClicked().getName())) {
//                String bluetag = e.getWhoClicked().getScoreboardTags().stream().filter(tag -> tag.contains("bluered.")).findFirst().map(tag -> tag.substring(8)).orElse("");
//                Blueprint blueprint = Sxlib.get().blueprintMap.get(bluetag);
//                Path.of("blueprints").toFile().mkdir();
//                for (ItemStack item : e.getClickedInventory().getStorageContents()) {
//                    Bukkit.getServer().sendMessage(item.displayName());
//                }
//                try (FileWriter fileWriter = new FileWriter(Path.of("blueprints", PlainTextComponentSerializer.plainText().serialize(e.getCurrentItem().displayName())+".blueprint").toFile())) {
//                    fileWriter.write(blueprint.getBlocks().toString());
//                    fileWriter.flush();
//                } catch (IOException ex) {
//                    throw new RuntimeException(ex);
//                }
//            }
//            stringGetter.run();
//            e.getWhoClicked().closeInventory();
//        }
//    }


    @EventHandler
    public void PlTic(PlayerInteractEvent e) {
        if (Sxlib.get().titleTickers.contains(e.getPlayer().getName())) {


            Player player = e.getPlayer();

            String bluetag = player.getScoreboardTags().stream().filter(tag -> tag.contains("bluered.")).findFirst().map(tag -> tag.substring(8)).orElse("");


            BlueprintBuilder blueprintBuilder = Sxlib.get().blueprintMap.get(bluetag);
            Runnable runnable = () -> {
                player.removeScoreboardTag("bluered." + bluetag);
                Sxlib.get().titleTickers.remove(player.getName());
                blueprintBuilder.outline.deleteAll();
            };
            if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {

//
//                StringGetter stringGetter = new StringGetter(runnable, player);
//                stringGetter.open();
                AnvilGUI.Builder builder = new AnvilGUI.Builder();
                builder.text("Blueprint name");
                builder.itemLeft(Buttons.blank());
                builder.title("Type blueprint name");
                ItemStack stack = new ItemStack(Material.NAME_TAG);
                ItemMeta meta = stack.getItemMeta();
                meta.setItemModel(NamespacedKey.fromString("general:button/accept"));
                meta.displayName(Component.text("Name blueprint"));
                meta.setHideTooltip(true);
                stack.setItemMeta(meta);
                builder.itemOutput(stack);

                builder.plugin(JavaPlugin.getPlugin(Sxlib.class));
                builder.onClick(( slot,stateSnapshot) -> {
                Path.of("blueprints").toFile().mkdir();
                try (FileWriter fileWriter = new FileWriter(Path.of("blueprints", stateSnapshot.getText()+".blueprint").toFile())) {
                    fileWriter.write(blueprintBuilder.getBlocks().toString());
                    fileWriter.flush();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                    runnable.run();
                    stateSnapshot.getPlayer().sendMessage(Component.text(stateSnapshot.getText()));
                    stateSnapshot.getPlayer().closeInventory();
                    return Arrays.asList(AnvilGUI.ResponseAction.close());
                });
                builder.open(player);
                return;
            }

            runnable.run();

            e.setCancelled(true);
        }
    }

    @EventHandler
    public void Gweda(InventoryOpenEvent e) {
        if (Sxlib.get().titleTickers.contains(e.getPlayer().getName())) {
            e.getPlayer().closeInventory();
        }
    }

    @EventHandler
    public void onPlayerScroll(PlayerItemHeldEvent event) {

        boolean redactor = false;
        String bluetag = "";
        for (String tag : event.getPlayer().getScoreboardTags()) {
            if (tag.contains("bluered.")) {
                redactor = true;
                bluetag = tag;
                break;
            }
        }
        ;

        if (redactor) {

            int previousSlot = event.getPreviousSlot();
            int newSlot = event.getNewSlot();

            String direction;

            if ((newSlot > previousSlot)) {
                direction = "down";
            } else if ((newSlot < previousSlot)) {
                direction = "up";
            } else {
                direction = "hz"; // На случай ошибок (быть не должно)
            }
            BlueprintBuilder blueprintBuilder = Sxlib.get().blueprintMap.get(bluetag.substring(8));

            Map<String, Vector> h = getNearestPointOnFace(event.getPlayer().getLocation().add(0, event.getPlayer().getEyeHeight(),0), blueprintBuilder.vertex1.toVector(), blueprintBuilder.vertex2.toVector());


            Vector v1 = blueprintBuilder.vertex1.toVector();
            Vector v2 = blueprintBuilder.vertex2.toVector();

            Vector c = h.get("cord");
            Vector d = h.get("direction");

            d.multiply(Math.abs(newSlot - previousSlot));


            if (direction.equals("up")) d.multiply(-1);
            if (isInside(event.getPlayer().getLocation(), v1, v2)) d.multiply(-1);

            if ((v1.getX() == c.getX()) || (v1.getY() == c.getY()) || (v1.getZ() == c.getZ())) {
                Location vec1 = blueprintBuilder.vertex1.clone().add(d);
                if (Math.abs(vec1.getX() - v2.getX()) >= 2 && Math.abs(vec1.getY() - v2.getY()) >= 2 && Math.abs(vec1.getZ() - v2.getZ()) >= 2) {
                    blueprintBuilder.vertex1 = vec1;
                }
                blueprintBuilder.resizeOutline();
            } else if ((v2.getX() == c.getX()) || (v2.getY() == c.getY()) || (v2.getZ() == c.getZ())) {
                Location vec1 = blueprintBuilder.vertex2.clone().add(d);
                if (Math.abs(vec1.getX() - v1.getX()) >= 2 && Math.abs(vec1.getY() - v1.getY()) >= 2 && Math.abs(vec1.getZ() - v1.getZ()) >= 2) {
                    blueprintBuilder.vertex2 = vec1;
                }
                blueprintBuilder.resizeOutline();
            }
            if (d.equals(new Vector(0, 0, 0))) {
                blueprintBuilder.resizeOutline();
            }

            ParticleBuilder particleBuilder = new ParticleBuilder(Particle.WAX_OFF);
            particleBuilder.location(event.getPlayer().getLocation().add(0, event.getPlayer().getEyeHeight(),0));
            particleBuilder.spawn();

            event.setCancelled(true);
            event.getPlayer().getInventory().setHeldItemSlot(4);

        }
    }


    public static Map<String, Vector> getNearestPointOnFace(Location playerLocation, Vector point1, Vector point2) {
        // Определяем границы куба (AABB)
        double minX = Math.min(point1.getX(), point2.getX());
        double maxX = Math.max(point1.getX(), point2.getX());
        double minY = Math.min(point1.getY(), point2.getY());
        double maxY = Math.max(point1.getY(), point2.getY());
        double minZ = Math.min(point1.getZ(), point2.getZ());
        double maxZ = Math.max(point1.getZ(), point2.getZ());

        // Позиция игрока и направление взгляда
        Vector rayOrigin = playerLocation.toVector();
        Vector rayDir = playerLocation.getDirection().normalize();

        // Ищем пересечение луча с каждой плоскостью куба
        double[] tValues = new double[6];
        Vector[] hitPoints = new Vector[6];
        Vector[] faceNormals = {
                new Vector(-1, 0, 0), new Vector(1, 0, 0),  // Лево, право
                new Vector(0, -1, 0), new Vector(0, 1, 0),  // Низ, верх
                new Vector(0, 0, -1), new Vector(0, 0, 1)   // Задняя, передняя грань
        };

        double tMin = Double.POSITIVE_INFINITY;
        Vector nearestHit = null;
        Vector nearestNormal = null;

        // Проверяем пересечение с каждой гранью
        for (int i = 0; i < 6; i++) {
            double t = 0;
            Vector planePoint = new Vector();

            switch (i) {
                case 0:
                    t = (minX - rayOrigin.getX()) / rayDir.getX();
                    planePoint = new Vector(minX, 0, 0);
                    break; // LEFT
                case 1:
                    t = (maxX - rayOrigin.getX()) / rayDir.getX();
                    planePoint = new Vector(maxX, 0, 0);
                    break; // RIGHT
                case 2:
                    t = (minY - rayOrigin.getY()) / rayDir.getY();
                    planePoint = new Vector(0, minY, 0);
                    break; // BOTTOM
                case 3:
                    t = (maxY - rayOrigin.getY()) / rayDir.getY();
                    planePoint = new Vector(0, maxY, 0);
                    break; // TOP
                case 4:
                    t = (minZ - rayOrigin.getZ()) / rayDir.getZ();
                    planePoint = new Vector(0, 0, minZ);
                    break; // BACK
                case 5:
                    t = (maxZ - rayOrigin.getZ()) / rayDir.getZ();
                    planePoint = new Vector(0, 0, maxZ);
                    break; // FRONT
            }

            if (t > 0) { // Проверяем, что точка перед игроком
                Vector hitPoint = rayOrigin.clone().add(rayDir.clone().multiply(t));
                ParticleBuilder particleBuilder = new ParticleBuilder(Particle.WAX_OFF);
                particleBuilder.location(new Location(playerLocation.getWorld(),hitPoint.getX(),hitPoint.getY(),hitPoint.getZ()));
                particleBuilder.spawn();
                // Проверяем, находится ли точка пересечения внутри границ куба
                if (hitPoint.getX() >= minX && hitPoint.getX() <= maxX &&
                        hitPoint.getY() >= minY && hitPoint.getY() <= maxY &&
                        hitPoint.getZ() >= minZ && hitPoint.getZ() <= maxZ) {

                    // Выбираем ближайшую точку пересечения
                    if (t < tMin) {
                        tMin = t;
                        nearestHit = hitPoint;
                        nearestNormal = faceNormals[i];
                    }
                }
            }
        }

        if (nearestHit == null) {
            return Map.of("cord", new Vector(0, 0, 0), "direction", new Vector(0, 0, 0));
        }


        return Map.of("cord", nearestHit, "direction", nearestNormal);
    }


    // Проверка, лежит ли точка внутри грани
    private static boolean isInside(Location playerLocation, Vector point1, Vector point2) {

        double minX = Math.min(point1.getX(), point2.getX());
        double maxX = Math.max(point1.getX(), point2.getX());
        double minY = Math.min(point1.getY(), point2.getY());
        double maxY = Math.max(point1.getY(), point2.getY());
        double minZ = Math.min(point1.getZ(), point2.getZ());
        double maxZ = Math.max(point1.getZ(), point2.getZ());

        return playerLocation.getX() <= maxX && playerLocation.getX() >= minX &&
                playerLocation.getY() <= maxY && playerLocation.getY() >= minY &&
                playerLocation.getZ() <= maxZ && playerLocation.getZ() >= minZ;
    }
}
