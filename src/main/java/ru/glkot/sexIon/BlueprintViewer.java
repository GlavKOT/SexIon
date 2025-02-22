package ru.glkot.sexIon;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.Matrix4f;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.lang.model.util.Elements;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class BlueprintViewer {

    public Map<JSONObject,BlockDisplay> displays;
    private List<JSONObject> list;
    private File file;
    private String string;
    public Vector maxCords;
    public Outline outline;
    public Location origin;
    public int rotation;
    public Vector mn;
    public Vector mx;

    public BlueprintViewer(String path) {
        file = Path.of("blueprints",path).toFile();
        displays = new HashMap<>();
        rotation = 0;
        try (FileInputStream zov = new FileInputStream(file)) {
            string = new String(zov.readAllBytes());
            JSONArray array = new JSONArray(string);
            list = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                list.add(array.getJSONObject(i));
            }
            maxCords = getMaxCoordinates(string);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mn = getMinCord();
        mx = getMaxCord();
    }

    public void rotate(int rot) {
        if (rot == 1) {
            switch (rotation) {
                case 0 -> rotation = 1;
                case 1 -> rotation = 2;
                case 2 -> rotation = 3;
                case 3 -> rotation = 0;
            }
        } else {
            switch (rotation) {
                case 0 -> rotation = 3;
                case 1 -> rotation = 0;
                case 2 -> rotation = 1;
                case 3 -> rotation = 2;
            }
        }
        for (Map.Entry<JSONObject,BlockDisplay> entry : displays.entrySet()) {
            switch (rotation) {
                case 0 -> {
                    entry.getValue().teleport(origin.clone().add(entry.getKey().getInt("x") + .5,entry.getKey().getInt("y") + .5,entry.getKey().getInt("z") + .5));
                    entry.getValue().setRotation(0,0);
                }
                case 1 -> {
                    entry.getValue().teleport(origin.clone().add(-entry.getKey().getInt("z") + .5, entry.getKey().getInt("y") + .5, entry.getKey().getInt("x") + .5));
                    entry.getValue().setRotation(90,0);
                }
                case 2 -> {
                    entry.getValue().teleport(origin.clone().add(-entry.getKey().getInt("x") + .5, entry.getKey().getInt("y") + .5, -entry.getKey().getInt("z") + .5));
                    entry.getValue().setRotation(180,0);
                }
                case 3 -> {
                    entry.getValue().teleport(origin.clone().add(entry.getKey().getInt("z") + .5, entry.getKey().getInt("y") + .5, -entry.getKey().getInt("x") + .5));
                    entry.getValue().setRotation(270,0);
                }
                default -> throw new RuntimeException("Rotation is invalid");
            }
        }

        mn = getMinCord();
        mx = getMaxCord();
        outline.resize(mn.getX(),mn.getY(),mn.getZ(),mx.getX(),mx.getY(),mx.getZ());
    }

    public void setup(Location location) {
        origin = location;
        outline = new Outline(origin.getBlockX(),origin.getBlockY(),origin.getBlockZ(),origin.getBlockX()+maxCords.getBlockX(),origin.getBlockY()+ maxCords.getBlockY(),origin.getBlockZ()+maxCords.getBlockZ(),origin.getWorld(), UUID.randomUUID());
        for (JSONObject jsonObject : list) {
            convertToBlockDisplay(jsonObject,location,0);
        }
        outline.spawn();
    }


    public void convertToBlockDisplay(JSONObject json, Location offset, int rotation) {
        World world = offset.getWorld();

        if (world == null) {
            System.out.println("Мир не найден!");
            return;
        }

        // Извлекаем координаты из JSON
        double x = json.getDouble("x");
        double y = json.getDouble("y");
        double z = json.getDouble("z");

        // Итоговая позиция с учетом смещения
        Location location = new Location(world, x + offset.getX() + .5, y + offset.getY() + .5, z + offset.getZ() +.5);

        // Извлекаем блок из JSON
        String blockState = json.getString("state");
        BlockData blockData = Bukkit.createBlockData(blockState);

        // Создаем BlockDisplay
        BlockDisplay display = world.spawn(location, BlockDisplay.class);
        display.setBlock(blockData);
        display.setRotation(rotation, 0); // Устанавливаем поворот
        display.setTransformationMatrix(new Matrix4f().setTranslation(-.5f,-.5f,-.5f));
        display.setTeleportDuration(4);

        display.setBrightness(new Display.Brightness(15,15));

        displays.put(json,display);

        System.out.println("Создан BlockDisplay: " + blockData + " на " + location);
    }

    public static Vector getMaxCoordinates(String jsonArrayString) {
        JSONArray jsonArray = new JSONArray(jsonArrayString);
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (jsonObject.has("x") && jsonObject.has("y") && jsonObject.has("z")) {
                int x = jsonObject.getInt("x");
                int y = jsonObject.getInt("y");
                int z = jsonObject.getInt("z");

                maxX = Math.max(maxX, x);
                maxY = Math.max(maxY, y);
                maxZ = Math.max(maxZ, z);
            }
        }

        return new Vector(maxX, maxY, maxZ);
    }

    public void place() {
        for (BlockDisplay bd : displays.values()) {
            BlockData blockData = bd.getBlock();
            String blockDataString = blockData.getAsString();
            if (blockDataString.contains("[") && blockDataString.contains("]")) {
                switch (rotation) {
                    case 1 -> blockDataString = blockDataString
                            .replaceAll("east", "tempast")
                            .replaceAll("north", "temprth")
                            .replaceAll("west", "tempest")
                            .replaceAll("south", "tempouth")
                            .replaceAll("tempast", "south")
                            .replaceAll("temprth", "east")
                            .replaceAll("tempest", "north")
                            .replaceAll("tempouth", "west");

                    case 2 -> blockDataString = blockDataString
                            .replaceAll("east", "tempast")
                            .replaceAll("north", "temprth")
                            .replaceAll("west", "tempest")
                            .replaceAll("south", "tempouth")
                            .replaceAll("tempast", "west")
                            .replaceAll("temprth", "south")
                            .replaceAll("tempest", "east")
                            .replaceAll("tempouth", "north");

                    case 3 -> blockDataString = blockDataString
                            .replaceAll("east", "tempast")
                            .replaceAll("north", "temprth")
                            .replaceAll("west", "tempest")
                            .replaceAll("south", "tempouth")
                            .replaceAll("tempast", "north")
                            .replaceAll("temprth", "west")
                            .replaceAll("tempest", "south")
                            .replaceAll("tempouth", "east");

                }
            }
            BlockData ref = Bukkit.createBlockData(blockDataString);
            bd.getLocation().getBlock().setBlockData(ref, false);
        }
    }

    public Vector getMaxCord() {
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;

        for (Map.Entry<JSONObject,BlockDisplay> entry : displays.entrySet()) {
            maxX = (int) Math.max(maxX,entry.getValue().getLocation().getX() -.5);
            maxY = (int) Math.max(maxY,entry.getValue().getLocation().getY() -.5);
            maxZ = (int) Math.max(maxZ,entry.getValue().getLocation().getZ() -.5);
        }
        return new Vector(maxX, maxY, maxZ);
    }

    public Vector getMinCord() {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;

        for (Map.Entry<JSONObject,BlockDisplay> entry : displays.entrySet()) {
            minX = (int) Math.min(minX,entry.getValue().getLocation().getX() -.5);
            minY = (int) Math.min(minY,entry.getValue().getLocation().getY() -.5);
            minZ = (int) Math.min(minZ,entry.getValue().getLocation().getZ() -.5);
        }
        System.out.println( new Vector(minX, minY, minZ).toString());
        return new Vector(minX, minY, minZ);
    }

}
