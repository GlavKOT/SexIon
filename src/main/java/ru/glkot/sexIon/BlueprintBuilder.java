package ru.glkot.sexIon;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

public class BlueprintBuilder {

    public BlueprintBuilder(Location loc1, Location loc2) {
        vertex1 = loc1;
        vertex2 = loc2;
        uuid = UUID.randomUUID();
    }

    private BlueprintBuilder(Location v1, Location v2, String id) {
        vertex1 = v1;
        vertex2 = v2;
        uuid = UUID.fromString(id);

    }

    Location vertex1;
    Location vertex2;

    public Outline outline;


    private int minX() {
        return Math.min(vertex1.getBlockX(), vertex2.getBlockX());
    }
    private int maxX () {
        return Math.max(vertex1.getBlockX(), vertex2.getBlockX());
    }
    private int minY () {
        return Math.min(vertex1.getBlockY(), vertex2.getBlockY());
    }
    private int maxY () {
            return Math.max(vertex1.getBlockY(), vertex2.getBlockY());
    }
    private int minZ () {
        return Math.min(vertex1.getBlockZ(), vertex2.getBlockZ());
    }
    private int maxZ () {
        return Math.max(vertex1.getBlockZ(), vertex2.getBlockZ());
    }
    private List<String> blocks;
    private UUID uuid;


    public void genOutline() {
        outline = new Outline(minX(),minY(),minZ(),maxX(),maxY(),maxZ(), vertex2.getWorld(), uuid);
        outline.spawn();
    }

    public void resizeOutline() {
        outline.resize(minX()+randD(),minY()+randD(),minZ()+randD(),maxX()+randD(),maxY()+randD(),maxZ()+randD());
    }

    public double randD() {
        Random random = new Random();
        return random.nextDouble(-0.01,0.01);
    }

//    public void redactor(Player player) {
//            genOutline();
//            player.addScoreboardTag("bluered."+uuid);
//            File tempbluedir = Path.of("bluetemp").toFile();
//            tempbluedir.mkdir();
//            JSONObject blueprint = new JSONObject();
//            blueprint.put("v1",vertex1.toVector().serialize());
//            blueprint.put("v2",vertex2.toVector().serialize());
//            blueprint.put("id",uuid);
//            File tempfile = Path.of("bluetemp",uuid.toString()).toFile();
//            try (FileWriter writer = new FileWriter(tempfile)) {
//                writer.write(blueprint.toString(4));
//                writer.flush();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//    }

    public void redactor(Player player) {

        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(""));
        meta.setHideTooltip(true);
        meta.setItemModel(NamespacedKey.fromString("general:button/nothing"));
        item.setItemMeta(meta);



        Map<Integer, ItemStack> items = new HashMap<>();
        for (int i = 0; i<=8;i++) {
            items.put(i,player.getInventory().getItem(i));
            player.getInventory().setItem(i,item);
        }

        Sxlib.get().oldHotBars.put(player.getName(),items);

        genOutline();
        player.addScoreboardTag("bluered."+uuid);
        Sxlib.get().titleTickers.add(player.getName());
        flush();
    }

    public void flush() {
        Sxlib.get().blueprintMap.put(uuid.toString(),this);
    }

    public static BlueprintBuilder getFromID (String id, World world) {
        File file = Path.of("bluetemp",id).toFile();
        try (FileInputStream stream = new FileInputStream(file)) {
            JSONObject js = new JSONObject(new String(stream.readAllBytes()));

            for (ItemDisplay itemDisplay: world.getEntitiesByClass(ItemDisplay.class)) {
                if (itemDisplay.getName().equals("outline:"+id)) itemDisplay.remove();
            }

            JSONObject v1 = js.getJSONObject("v1");
            JSONObject v2 = js.getJSONObject("v2");

            return new BlueprintBuilder(new Location(world,v1.getInt("x"),v1.getInt("y"),v1.getInt("z")),new Location (world, v2.getInt("x"),v2.getInt("y"),v2.getInt("z")),id);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Преобразовать блок в JSON
    public static JSONObject blockToJson(Block block, Vector baseVector) {
        Vector relativeVector = block.getLocation().toVector().subtract(baseVector);
        JSONObject json = new JSONObject();

        // Координаты
        json.put("x", relativeVector.getX());
        json.put("y", relativeVector.getY());
        json.put("z", relativeVector.getZ());

        // Тип блока
        json.put("type", block.getType().toString());

        // Состояние блока
        json.put("state", block.getBlockData().getAsString());

        return json;
    }

    public void jsonToBlock(JSONObject json, Location location, boolean centered, String rotation) {
        System.out.println(json.toString(4));

        Location loc;
        switch (rotation) {
            case "0" ->
                    loc = new Location(location.getWorld(), json.getInt("x"), json.getInt("y"), json.getInt("z")).add(location);
            case "90" ->
                    loc = new Location(location.getWorld(), -json.getInt("z") + (maxZ() - minZ()), json.getInt("y"), json.getInt("x")).add(location);
            case "180" ->
                    loc = new Location(location.getWorld(), -json.getInt("x") + (maxZ() - minZ()), json.getInt("y"), -json.getInt("z") + (maxX() - minX())).add(location);
            case "270" ->
                    loc = new Location(location.getWorld(), json.getInt("z"), json.getInt("y"), -json.getInt("x") + (maxZ() - minZ())).add(location);
            default -> throw new RuntimeException("Rotation is invalid");
        }

        Material material = Material.valueOf(json.getString("type"));
        Block block = loc.getBlock();
        block.setType(material, false);

        if (json.has("state")) {
            String blockDataString = json.getString("state");
            if (blockDataString.contains("[") && blockDataString.contains("]")) {
                switch (rotation) {
                    case "90" -> blockDataString = blockDataString
                            .replaceAll("east", "tempast")
                            .replaceAll("north", "temprth")
                            .replaceAll("west", "tempest")
                            .replaceAll("south", "tempouth")
                            .replaceAll("tempast", "south")
                            .replaceAll("temprth", "east")
                            .replaceAll("tempest", "north")
                            .replaceAll("tempouth", "west");

                    case "180" -> blockDataString = blockDataString
                            .replaceAll("east", "tempast")
                            .replaceAll("north", "temprth")
                            .replaceAll("west", "tempest")
                            .replaceAll("south", "tempouth")
                            .replaceAll("tempast", "west")
                            .replaceAll("temprth", "south")
                            .replaceAll("tempest", "east")
                            .replaceAll("tempouth", "north");

                    case "270" -> blockDataString = blockDataString
                            .replaceAll("east", "tempast")
                            .replaceAll("north", "temprth")
                            .replaceAll("west", "tempest")
                            .replaceAll("south", "tempouth")
                            .replaceAll("tempast", "north")
                            .replaceAll("temprth", "west")
                            .replaceAll("tempest", "south")
                            .replaceAll("tempouth", "east");

                }


                org.bukkit.block.data.BlockData blockData = Bukkit.createBlockData(material, blockDataString.substring(blockDataString.indexOf("["), blockDataString.indexOf("]") + 1));
                block.setBlockData(blockData, false);
            }
        }
    }

    // Получить все блоки между двумя точками
    public List<String> getBlocks() {
        List<Block> blocks = new ArrayList<>();

        World world = vertex1.getWorld();
        List<String> blockInfoList = new ArrayList<>();

        for (int x = minX(); x <= maxX(); x++) {
            for (int y = minY(); y <= maxY(); y++) {
                for (int z = minZ(); z <= maxZ(); z++) {
                    Block block = world.getBlockAt(x, y, z);

                    if (!block.isEmpty()) {
                        JSONObject blockJson = blockToJson(block, new Vector(minX(), minY(), minZ()));
                        blockInfoList.add(blockJson.toString());
                    }
                }
            }
        }

        return blockInfoList;
    }

    public UUID getId() {
        return uuid;
    }

    public void temp() {
            File tempbluedir = Path.of("bluetemp").toFile();
            tempbluedir.mkdir();
            JSONObject blueprint = new JSONObject();
            blueprint.put("v1",vertex1.toVector().serialize());
            blueprint.put("v2",vertex2.toVector().serialize());
            blueprint.put("id",uuid);
            File tempfile = Path.of("bluetemp",uuid.toString()).toFile();
            try (FileWriter writer = new FileWriter(tempfile)) {
                writer.write(blueprint.toString(4));
                writer.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }

    // Получить список List<List<String>> для всех блоков между двумя точками
    public static List<String> getBlockInfoList(List<Block> blocks, Vector baseVector) {
        List<String> blockInfoList = new ArrayList<>();
        for (Block block : blocks) {
            JSONObject blockJson = blockToJson(block, baseVector);
            blockInfoList.add(blockJson.toString());
        }

        return blockInfoList;
    }

    public void place(Location loc, boolean centered, String rotation) {
        for (String s : blocks) {
            jsonToBlock(new JSONObject(s), loc, centered, rotation);
        }
    }
}
