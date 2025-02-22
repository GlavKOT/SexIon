package ru.glkot.sexIon;

import com.destroystokyo.paper.ParticleBuilder;
import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.*;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Item;
import org.bukkit.entity.ItemDisplay;
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
import org.joml.Intersectiond;
import org.joml.Vector2d;
import org.joml.Vector3d;
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
        if (Sxlib.get().blueprintViewers.containsKey(e.getPlayer().getName())) {
            if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
                BlueprintViewer blueprintViewer = Sxlib.get().blueprintViewers.get(e.getPlayer().getName());
                blueprintViewer.place();
            } else {
                BlueprintViewer blueprintViewer = Sxlib.get().blueprintViewers.get(e.getPlayer().getName());
                for (BlockDisplay boba : blueprintViewer.displays.values()) {
                    boba.remove();
                }
                for (UUID itemDisplay : blueprintViewer.outline.getFaces().values()) {
                    Bukkit.getEntity(itemDisplay).remove();
                }
                Sxlib.get().blueprintViewers.remove(e.getPlayer().getName());
                for (Map.Entry<Integer, ItemStack> entry : Sxlib.get().oldHotBars.get(e.getPlayer().getName()).entrySet()) {
                    e.getPlayer().getInventory().setItem(entry.getKey(),entry.getValue());
                }
            }
        }

        if (Sxlib.get().titleTickers.contains(e.getPlayer().getName())) {


            Player player = e.getPlayer();

            String bluetag = player.getScoreboardTags().stream().filter(tag -> tag.contains("bluered.")).findFirst().map(tag -> tag.substring(8)).orElse("");


            BlueprintBuilder blueprintBuilder = Sxlib.get().blueprintMap.get(bluetag);
            Runnable runnable = () -> {
                player.removeScoreboardTag("bluered." + bluetag);
                Sxlib.get().titleTickers.remove(player.getName());
                blueprintBuilder.outline.deleteAll();
                for (Map.Entry<Integer, ItemStack> entry : Sxlib.get().oldHotBars.get(e.getPlayer().getName()).entrySet()) {
                    player.getInventory().setItem(entry.getKey(),entry.getValue());
                }
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
                Path.of("blueprints", stateSnapshot.getText()+".blueprint").toFile().getParentFile().mkdirs();
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

            Map<String, Vector3d> h = getNearestPointOnFaceJOML(event.getPlayer().getLocation().add(0, event.getPlayer().getEyeHeight(),0), blueprintBuilder.vertex1.toVector().toVector3d(), blueprintBuilder.vertex2.toVector().toVector3d());

            System.out.println(h);
            Vector v1 = blueprintBuilder.vertex1.toVector();
            Vector v2 = blueprintBuilder.vertex2.toVector();

            Vector c = Vector.fromJOML(h.get("cord"));
            Vector d = Vector.fromJOML(h.get("direction"));

            System.out.println(c);
            System.out.println(d);

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

        if (Sxlib.get().blueprintViewers.containsKey(event.getPlayer().getName())) {
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
            BlueprintViewer blueprintViewer = Sxlib.get().blueprintViewers.get(event.getPlayer().getName());

            Vector v1 = blueprintViewer.getMinCord();
            Vector v2 = blueprintViewer.getMaxCord().add(new Vector(1,1,1));

            System.out.println(v1);
            System.out.println(v2);

            Map<String, Vector3d> h = getNearestPointOnFaceJOML(event.getPlayer().getLocation().add(0, event.getPlayer().getEyeHeight(),0).clone(), v1.clone().toVector3d(), v2.clone().toVector3d());




            Vector c = Vector.fromJOML(h.get("cord")).clone();
            Vector d = Vector.fromJOML(h.get("direction")).clone();

            d.multiply(Math.abs(newSlot - previousSlot));


            if (direction.equals("up")) d.multiply(-1);
            if (isInside(event.getPlayer().getLocation(), v1, v2)) d.multiply(-1);

            System.out.println(v1);
            System.out.println(v2);
            System.out.println(h);

            blueprintViewer.origin.add(d);
            for (BlockDisplay b : blueprintViewer.displays.values()) {
                b.setInterpolationDelay(0);
                b.setInterpolationDuration(4);
                b.setTeleportDuration(4);
                b.teleport(b.getLocation().add(d));
            }
            for (UUID uuid : blueprintViewer.outline.getIDS()) {
                Bukkit.getEntity(uuid).teleport(Bukkit.getEntity(uuid).getLocation().add(d));
            }
            if (d.isZero()) {
                if (direction.equals("up")) blueprintViewer.rotate(1);
                    else blueprintViewer.rotate(2);
            }
            ParticleBuilder particleBuilder = new ParticleBuilder(Particle.WAX_OFF);
            particleBuilder.location(event.getPlayer().getLocation().add(0, event.getPlayer().getEyeHeight(),0));
            particleBuilder.spawn();

            event.setCancelled(true);
            event.getPlayer().getInventory().setHeldItemSlot(4);

        }
    }

    public static Map<String, Vector3d> getNearestPointOnFaceJOML(Location playerLocation, Vector3d point1, Vector3d point2) {
        // Определяем min/max координаты куба
        Vector3d min = new Vector3d(Math.min(point1.x, point2.x), Math.min(point1.y, point2.y), Math.min(point1.z, point2.z));
        Vector3d max = new Vector3d(Math.max(point1.x, point2.x), Math.max(point1.y, point2.y), Math.max(point1.z, point2.z));

        // Начальная точка и направление луча
        Vector3d rayOrigin = new Vector3d(playerLocation.getX(), playerLocation.getY(), playerLocation.getZ());
        Vector3d rayDir = new Vector3d(playerLocation.getDirection().getX(),
                playerLocation.getDirection().getY(),
                playerLocation.getDirection().getZ()).normalize();

        Vector2d result = new Vector2d();
        boolean intersects = Intersectiond.intersectRayAab(rayOrigin, rayDir, min, max, result);

        if (!intersects) {
            return Map.of("cord", new Vector3d(0, 0, 0), "direction", new Vector3d(0, 0, 0));
        }

        double t = result.x; // Ближайшая точка пересечения
        Vector3d hitPoint = rayOrigin.add(rayDir.mul(t, new Vector3d())); // Вычисляем точку

        // Определяем, в какую грань попали
        Vector3d normal = new Vector3d(0, 0, 0);

        if (Math.abs(hitPoint.x - min.x) < 0.001) normal.set(-1, 0, 0); // Левая грань
        else if (Math.abs(hitPoint.x - max.x) < 0.001) normal.set(1, 0, 0); // Правая грань
        else if (Math.abs(hitPoint.y - min.y) < 0.001) normal.set(0, -1, 0); // Нижняя грань
        else if (Math.abs(hitPoint.y - max.y) < 0.001) normal.set(0, 1, 0); // Верхняя грань
        else if (Math.abs(hitPoint.z - min.z) < 0.001) normal.set(0, 0, -1); // Задняя грань
        else if (Math.abs(hitPoint.z - max.z) < 0.001) normal.set(0, 0, 1); // Передняя грань

        return Map.of("cord", hitPoint, "direction", normal);
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
