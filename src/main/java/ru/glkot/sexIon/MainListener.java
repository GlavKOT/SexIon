package ru.glkot.sexIon;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import ru.glkot.sexIon.playerdata.PlayerData;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

public class MainListener implements Listener {

    @EventHandler
    public void huy(PlayerJoinEvent e) {
        new PlayerData(e.getPlayer().getUniqueId());

        // Sending RP
        try {

            FileInputStream zov = new FileInputStream(Path.of(Path.of("").toAbsolutePath().toString(),"rp.hash").toFile());

            ResourcePackRequest.@NotNull Builder resourcePackRequest = ResourcePackRequest.resourcePackRequest();
            ResourcePackInfo resourcePackInfo = ResourcePackInfo.resourcePackInfo(UUID.fromString("b102e59c-9018-303a-8fb7-5079edb6cb63"), URI.create("http://103.137.251.165:8080/RP.zip"),new String(zov.readAllBytes()));

            resourcePackRequest.packs(resourcePackInfo);

            e.getPlayer().sendResourcePacks(resourcePackRequest.asResourcePackRequest());

        } catch (IOException ev) {
            throw new RuntimeException(ev);
        }
    }

    @EventHandler
    public void TickerTitleBlueprint(ServerTickStartEvent event) {
        for (String s: Sxlib.get().titleTickers) {
            Player player = Bukkit.getPlayerExact(s);
            if (player!=null) {
                player.showTitle(Title.title(Component.text("\uE020\uE036").font(NamespacedKey.fromString("minecraft:default"))
                        .append(Component.translatable("space.100").font(NamespacedKey.fromString("space:default")))
                        .append(Component.text("\uE09E\uE03C").font(NamespacedKey.fromString("minecraft:default")))
                        ,Component.text(""), Title.Times.times(Duration.ZERO,Duration.ofSeconds(1),Duration.ZERO)));

            }


        }
    }

    @EventHandler
    public void Getter(PrepareAnvilEvent e) {
        AnvilInventory inventory = e.getInventory();
        HumanEntity humanEntity = e.getViewers().get(0);
        humanEntity.sendMessage("efaw");
        if (Sxlib.get().titleTickers.contains(humanEntity.getName())) {

            ItemStack stack = new ItemStack(Material.NAME_TAG);
            ItemMeta meta = stack.getItemMeta();
            meta.setHideTooltip(true);
            stack.setItemMeta(meta);

            inventory.setFirstItem(stack);

            stack.setItemMeta(meta);
            inventory.setResult(stack);
            humanEntity.closeInventory();
            humanEntity.openInventory(inventory);
        }

    }

    @EventHandler
    public void GetterR(InventoryClickEvent e) {
        StringGetter stringGetter = Sxlib.get().stringGetterMap.get(e.getWhoClicked().getName());
        if (stringGetter != null) {
            if (Sxlib.get().titleTickers.contains(e.getWhoClicked().getName())) {

            }
            stringGetter.run();
            e.getWhoClicked().closeInventory();
        }
    }


    @EventHandler
    public void PlTic(PlayerInteractEvent e) {
        if (Sxlib.get().titleTickers.contains(e.getPlayer().getName())) {


            Player player = e.getPlayer();

            String bluetag = player.getScoreboardTags().stream().filter(tag -> tag.contains("bluered.")).findFirst().map(tag -> tag.substring(8)).orElse("");


            Blueprint blueprint = Sxlib.get().blueprintMap.get(bluetag);
            Runnable runnable = () -> {
                player.removeScoreboardTag("bluered."+bluetag);
                Sxlib.get().titleTickers.remove(player.getName());
                blueprint.outline.deleteAll();
            };
            if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {


                StringGetter stringGetter = new StringGetter(runnable,player);
                stringGetter.open();

                player.sendMessage(Component.text(blueprint.getBlocks().toString()));
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
        };

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
            Blueprint blueprint = Sxlib.get().blueprintMap.get(bluetag.substring(8));

            Map<String, Vector> h = getNearestPointOnFace(event.getPlayer().getLocation(),blueprint.vertex1.toVector(),blueprint.vertex2.toVector());


            Vector v1 = blueprint.vertex1.toVector();
            Vector v2 = blueprint.vertex2.toVector();

            Vector c = h.get("cord");
            Vector d = h.get("direction");

            d.multiply(Math.abs(newSlot-previousSlot));




            if (direction.equals("up")) d.multiply(-1);
            if (isInside(event.getPlayer().getLocation(),v1,v2)) d.multiply(-1);

            if ((v1.getX() == c.getX()) || (v1.getY() == c.getY()) || (v1.getZ() == c.getZ())) {
                Location vec1 = blueprint.vertex1.clone().add(d);
                if (Math.abs(vec1.getX() - v2.getX()) >= 2 && Math.abs(vec1.getY() - v2.getY()) >= 2 &&Math.abs(vec1.getZ() - v2.getZ()) >= 2) {
                    blueprint.vertex1 = vec1;
                }
                blueprint.resizeOutline();
            }
            else if ((v2.getX() == c.getX()) || (v2.getY() == c.getY()) || (v2.getZ() == c.getZ())) {
                Location vec1 = blueprint.vertex2.clone().add(d);
                if (Math.abs(vec1.getX() - v1.getX()) >= 2 && Math.abs(vec1.getY() - v1.getY()) >= 2 &&Math.abs(vec1.getZ() - v1.getZ()) >= 2) {
                    blueprint.vertex2 = vec1;
                }
                blueprint.resizeOutline();
            }
            if (d.equals(new Vector(0, 0, 0))) {
                blueprint.resizeOutline();
            }


            event.setCancelled(true);
            event.getPlayer().getInventory().setHeldItemSlot(4);

        }}

    // Определение грани куба
    public static Map<String, Vector> getNearestPointOnFace(Location playerLocation, Vector point1, Vector point2) {
        // Находим границы куба
        double minX = Math.min(point1.getX(), point2.getX());
        double maxX = Math.max(point1.getX(), point2.getX());
        double minY = Math.min(point1.getY(), point2.getY());
        double maxY = Math.max(point1.getY(), point2.getY());
        double minZ = Math.min(point1.getZ(), point2.getZ());
        double maxZ = Math.max(point1.getZ(), point2.getZ());

        // Позиция игрока
        Vector playerPos = playerLocation.toVector();
        Vector direction = playerLocation.getDirection();


        // Скалирование направления до пересечения с гранями
        Vector hitPoint = null;

        // Проверяем каждую грань куба (по всем осям)
        // Грани по оси X



        for (int t = 0 ; t < 100; t++) {

            Vector v = playerPos.clone().add(direction.clone().multiply(t).multiply(0.5));

            double x = v.getX();
            double y = v.getY();
            double z = v.getZ();


            if ((x > minX) && (x < maxX) && (y > minY) && (y < maxY) && (z > minZ - 1) && (z < minZ)) {
                return Map.of("cord",new Vector(0,0,minZ), "direction", new Vector(0,0,-1)) ;
            }
            if ((x > minX) && (x < maxX) && (y > minY) && (y < maxY) && (z > maxZ) && (z < maxZ+1)) {
                return Map.of("cord",new Vector(0,0,maxZ), "direction", new Vector(0,0,1));
            }
            if ((x > minX) && (x < maxX) && (z > minZ) && (z < maxZ) && (y > maxY) && (y < maxY+1)) {
                return Map.of("cord",new Vector(0,maxY,0), "direction", new Vector(0,1,0));
            }
            if ((x > minX) && (x < maxX) && (z > minZ) && (z < maxZ) && (y > minY-1) && (y < minY)) {
                return Map.of("cord",new Vector(0,minY,0), "direction", new Vector(0,-1,0));
            }
            if ((z > minZ) && (z < maxZ) && (y > minY) && (y < maxY) && (x > minX - 1) && (x < minX)) {
                return Map.of("cord",new Vector(minX,0,0), "direction", new Vector(-1,0,0));
            }
            if ((z > minZ) && (z < maxZ) && (y > minY) && (y < maxY) && (x > maxX ) && (x < maxX+1)) {
                return Map.of("cord",new Vector(maxX,0,0), "direction", new Vector(1,0,0));
            }
        }

        // Если ничего не найдено
        return Map.of("cord",new Vector(0,0,0),"direction",new Vector(0,0,0));
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
