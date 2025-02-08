package ru.glkot.sexIon;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.joml.Matrix4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Outline {
    private static final Logger log = LoggerFactory.getLogger(Outline.class);
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    private double minZ;
    private double maxZ;
    private final World world;
    private List<ItemDisplay> itemDisplays;
    private JavaPlugin plugin = JavaPlugin.getPlugin(Sxlib.class);
    private UUID id;

    private Map<Integer, UUID> faces;

    public Map<Integer,UUID> getFaces(){
        return faces;
    }

    public Outline(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, World world, UUID id) {
        faces = new HashMap<>();
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.world = world;
        this.id = id;
        itemDisplays = new ArrayList<>();
    }

    public List<UUID> getIDS() {
        List<UUID> output = new ArrayList<>();
        itemDisplays.forEach(itemDisplay -> output.add(itemDisplay.getUniqueId()));
        return output;
    }

    public void resize(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        spawn();
        Bukkit.getScheduler().getMainThreadExecutor(plugin).execute(() -> {
            faces.values().forEach(display -> {
                ((ItemDisplay) Bukkit.getEntity(display)).setInterpolationDuration(4);
                ((ItemDisplay) Bukkit.getEntity(display)).setInterpolationDelay(0);
            });
        });
    }


    private void spawnEntity(int id, double x, double y, double z, float v, float v1, double sx, double sy, double sz, String model) {
        ItemDisplay itemDisplay = world.spawn(new Location(world, x, y, z), ItemDisplay.class);
        itemDisplay.setRotation(v, v1);
        itemDisplay.setBrightness(new Display.Brightness(15, 15));
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        meta.setItemModel(new NamespacedKey("general", model));
        item.setItemMeta(meta);
        itemDisplay.setItemStack(item);
        itemDisplay.customName(Component.text("outline:" + id));
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.scale((float) sx, (float) sy, (float) sz);
        itemDisplay.setTransformationMatrix(matrix4f);
        itemDisplay.setTeleportDuration(4);
        itemDisplays.add(itemDisplay);
        faces.put(id, itemDisplay.getUniqueId());
    }

    public void deleteAll() {
        Bukkit.getScheduler().runTask(plugin,()  -> {
                    for (UUID itemDisplay : faces.values()) {
                        Bukkit.getEntity(itemDisplay).remove();
                    }
                }
        );
    }

    public void newThread() {
        Thread thread = new Thread(() -> {
            while (true) {

            }
        });
        thread.start();
        thread.getId();
    }

    public void spawnLines() {
        Bukkit.getScheduler().getMainThreadExecutor(plugin).execute(() -> {

            int id = 1;
            if (!faces.containsKey(id)) {
                spawnEntity(id, minX + ((maxX - minX) / 2) + .5, minY + .5, minZ + .5, 180, 0, maxX - minX, 1, 1, "blueprint_line");
            } else {
                Matrix4f matrix4f = new Matrix4f();
                matrix4f.scale((float) (maxX - minX), 1, 1);
                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).setTransformationMatrix(matrix4f);
                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).teleport(new Location(world, minX + ((maxX - minX) / 2) + .5, minY + .5, minZ + .5,180,0));
            }

            id = 2;
            if (!faces.containsKey(id)) {
                spawnEntity(id, minX + ((maxX - minX) / 2) + .5, minY + .5, maxZ + .5, 0, 0, maxX - minX, 1, 1, "blueprint_line");
            } else {
                Matrix4f matrix4f = new Matrix4f();
                matrix4f.scale((float) (maxX - minX) , 1, 1);
                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).setTransformationMatrix(matrix4f);
                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).teleport(new Location(world, minX + ((maxX - minX) / 2) + .5, minY + .5, maxZ + .5,0,0));
            }

            id = 3;
            if (!faces.containsKey(id)) {
                spawnEntity(id, minX + ((maxX - minX) / 2) + .5, maxY + .5, minZ + .5, 180, -90, maxX - minX, 1, 1, "blueprint_line");
            } else {
                Matrix4f matrix4f = new Matrix4f();
                matrix4f.scale((float) (maxX - minX), 1, 1);
                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).setTransformationMatrix(matrix4f);
                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).teleport(new Location(world, minX + ((maxX - minX) / 2) + .5, maxY + .5, minZ + .5,180,-90));
            }

            id = 4;
            if (!faces.containsKey(id)) {
                spawnEntity(id, minX + ((maxX - minX) / 2) + .5, maxY + .5, maxZ + .5, 0, -90, maxX - minX, 1, 1, "blueprint_line");
            } else {
                Matrix4f matrix4f = new Matrix4f();
                matrix4f.scale((float) (maxX - minX), 1, 1);
                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).setTransformationMatrix(matrix4f);
                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).teleport(new Location(world, minX + ((maxX - minX) / 2) + .5, maxY + .5, maxZ + .5,0,-90));
            }

            id = 5;
            if (!faces.containsKey(id)) {
                spawnEntity(id, minX + .5, minY + .5, minZ + ((maxZ - minZ) / 2) + .5, 90, 0, maxZ - minZ, 1, 1, "blueprint_line");
            } else {
                Matrix4f matrix4f = new Matrix4f();
                matrix4f.scale((float) (maxZ - minZ), 1, 1);
                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).setTransformationMatrix(matrix4f);
                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).teleport(new Location(world, minX + .5, minY + .5, minZ + ((maxZ - minZ) / 2) + .5,90,0));
            }

            id = 6;
            if (!faces.containsKey(id)) {
                spawnEntity(id, maxX + .5, minY + .5, minZ + ((maxZ - minZ) / 2) + .5, -90, 0, maxZ - minZ, 1, 1, "blueprint_line");
            } else {
                Matrix4f matrix4f = new Matrix4f();
                matrix4f.scale((float) (maxZ - minZ), 1, 1);
                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).setTransformationMatrix(matrix4f);
                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).teleport(new Location(world, maxX + .5, minY + .5, minZ + ((maxZ - minZ) / 2) + .5,-90,0));
            }

            id = 7;
            if (!faces.containsKey(id)) {
                spawnEntity(id, minX + .5, maxY + .5, minZ + ((maxZ - minZ) / 2) + .5, 90, -90, maxZ - minZ, 1, 1, "blueprint_line");
            } else {
                Matrix4f matrix4f = new Matrix4f();
                matrix4f.scale((float) (maxZ - minZ), 1, 1);
                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).setTransformationMatrix(matrix4f);
                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).teleport(new Location(world, minX + .5, maxY + .5, minZ + ((maxZ - minZ) / 2) + .5,90,-90));
            }
            id = 8;
            if (!faces.containsKey(id)) {
                spawnEntity(id, maxX + .5, maxY + .5, minZ + ((maxZ - minZ) / 2) + .5, -90, -90, maxZ - minZ, 1, 1, "blueprint_line");
            } else {
                Matrix4f matrix4f = new Matrix4f();
                matrix4f.scale((float) (maxZ - minZ), 1, 1);
                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).setTransformationMatrix(matrix4f);
                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).teleport(new Location(world, maxX + .5, maxY + .5, minZ + ((maxZ - minZ) / 2) + .5,-90,-90));
            }

            id = 9;
            if (!faces.containsKey(id)) {
                spawnEntity(id, minX + .5, minY + ((maxY - minY) / 2) + .5, minZ + .5, 180, 0, 1, maxY - minY, 1, "blueprint_line_y");
            } else {
                Matrix4f matrix4f = new Matrix4f();
                matrix4f.scale(1, (float) ((maxY - minY)), 1);
                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).setTransformationMatrix(matrix4f);
                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).teleport(new Location(world, minX + .5, minY + ((maxY - minY) / 2) + .5, minZ + .5,180,0));
            }

            id = 10;
            if (!faces.containsKey(id)) {
                spawnEntity(id, maxX + .5, minY + ((maxY - minY) / 2) + .5, minZ + .5, -90, 0, 1, maxY - minY, 1, "blueprint_line_y");
            } else {
                Matrix4f matrix4f = new Matrix4f();
                matrix4f.scale(1, (float) ((maxY - minY)), 1);
                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).setTransformationMatrix(matrix4f);
                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).teleport(new Location(world, maxX + .5, minY + ((maxY - minY) / 2) + .5, minZ + .5,-90,0));
            }

            id = 11;
            if (!faces.containsKey(id)) {
                spawnEntity(id, minX + .5, minY + ((maxY - minY) / 2) + .5, maxZ + .5, 90, 0, 1, maxY - minY, 1, "blueprint_line_y");
            } else {
                Matrix4f matrix4f = new Matrix4f();
                matrix4f.scale(1, (float) ((maxY - minY)), 1);
                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).setTransformationMatrix(matrix4f);
                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).teleport(new Location(world, minX + .5, minY + ((maxY - minY) / 2) + .5, maxZ + .5,90,0));
            }

            id = 12;
            if (!faces.containsKey(id)) {
                spawnEntity(id, maxX + .5, minY + ((maxY - minY) / 2) + .5, maxZ + .5, 0, 0, 1, maxY - minY, 1, "blueprint_line_y");
            } else {
                Matrix4f matrix4f = new Matrix4f();
                matrix4f.scale(1, (float) ((maxY - minY)), 1);
                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).setTransformationMatrix(matrix4f);
                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).teleport(new Location(world, maxX + .5, minY + ((maxY - minY) / 2) + .5, maxZ + .5,0,0));
            }





            id = 13;
            if (!faces.containsKey(id)) {
                spawnEntity(13, minX + .5, minY + .5, minZ + .5, 180, 0, 1, 1, 1, "blueprint_corner");
            } else {
                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).teleport(new Location(world, minX + .5, minY + .5, minZ + .5,180,0));
            }

            id = 14;
            if (!faces.containsKey(id)) {
                spawnEntity(id, minX + .5, maxY + .5, minZ + .5, 180, -90, 1, 1, 1, "blueprint_corner");
            } else {
                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).teleport(new Location(world, minX + .5, maxY + .5, minZ + .5,180,-90));
            }
            id = 15;
            if (!faces.containsKey(id)) {
                spawnEntity(id, minX + .5, minY + .5, maxZ + .5, 90, 0, 1, 1, 1, "blueprint_corner");
            } else {
                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).teleport(new Location(world, minX + .5, minY + .5, maxZ + .5,90,0));
            }
            id = 16;
            if (!faces.containsKey(id)) {
                spawnEntity(id, minX + .5, maxY + .5, maxZ + .5, 90, -90, 1, 1, 1, "blueprint_corner");
            } else {
                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).teleport(new Location(world, minX + .5, maxY + .5, maxZ + .5,90,-90));
            }
            id = 17;
            if (!faces.containsKey(id)) {
                spawnEntity(id, maxX + .5, minY + .5, minZ + .5, -90, 0, 1, 1, 1, "blueprint_corner");
            } else {
                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).teleport(new Location(world, maxX + .5, minY + .5, minZ + .5,-90,0));
            }
            id = 18;
            if (!faces.containsKey(id)) {
                spawnEntity(id, maxX + .5, maxY + .5, minZ + .5, -90, -90, 1, 1, 1, "blueprint_corner");
            } else {
                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).teleport(new Location(world, maxX + .5, maxY + .5, minZ + .5,-90,-90));
            }
            id = 19;
            if (!faces.containsKey(id)) {
                spawnEntity(id, maxX + .5, minY + .5, maxZ + .5, 0, 0, 1, 1, 1, "blueprint_corner");
            } else {
                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).teleport(new Location(world, maxX + .5, minY + .5, maxZ + .5,0,0));
            }
            id = 20;
            if (!faces.containsKey(id)) {
                spawnEntity(id, maxX + .5, maxY + .5, maxZ + .5, 0, -90, 1, 1, 1, "blueprint_corner");
            } else {
                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).teleport(new Location(world, maxX + .5, maxY + .5, maxZ + .5,0,-90));
            }

        });
    }

    public void spawnFaces() {
        Bukkit.getScheduler().getMainThreadExecutor(plugin).execute(() -> {


            int id = 21;
            if (!faces.containsKey(id)) {
                spawnEntity(id,minX + ((maxX-minX)/2) +.5,minY +.5,minZ + ((maxZ-minZ)/2) +.5,0,90,maxX-minX+1,maxZ-minZ+1,1,"blueprint_filler");
            } else {
                Matrix4f matrix4f = new Matrix4f();
                matrix4f.scale((float) (maxX-minX)+1, (float) (maxZ-minZ)+1,1F);

                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).setTransformationMatrix(matrix4f);
                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).teleport(new Location(world ,minX + ((maxX-minX)/2) +.5,minY +.5,minZ + ((maxZ-minZ)/2) +.5,0,90));
            }
            id = 22;
            if (!faces.containsKey(id)) {
                spawnEntity(id,minX + ((maxX-minX)/2) +.5,maxY +.5,minZ + ((maxZ-minZ)/2) +.5,0,-90,maxX-minX+1,maxZ-minZ+1,1,"blueprint_filler");
            } else {
                Matrix4f matrix4f = new Matrix4f();
                matrix4f.scale((float) (maxX-minX)+1, (float) (maxZ-minZ)+1,1F);

                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).setTransformationMatrix(matrix4f);
                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).teleport(new Location(world ,minX + ((maxX-minX)/2) +.5,maxY +.5,minZ + ((maxZ-minZ)/2) +.5,0,-90));
            }
            id = 23;
            if (!faces.containsKey(id)) {
                spawnEntity(id,minX + ((maxX-minX)/2) +.5,minY + ((maxY-minY)/2) +.5,minZ +.5,180,0,maxX-minX+1,maxY-minY+1,1,"blueprint_filler");
            } else {
                Matrix4f matrix4f = new Matrix4f();
                matrix4f.scale((float) (maxX-minX)+1, (float) (maxY-minY)+1,1F);

                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).setTransformationMatrix(matrix4f);
                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).teleport(new Location(world ,minX + ((maxX-minX)/2) +.5,minY + ((maxY-minY)/2) +.5,minZ +.5,180,0));
            }
            id = 24;
            if (!faces.containsKey(id)) {
                spawnEntity(id,minX + ((maxX-minX)/2) +.5,minY + ((maxY-minY)/2) +.5,maxZ +.5,0,0,maxX-minX+1,maxY-minY+1,1,"blueprint_filler");
            } else {
                Matrix4f matrix4f = new Matrix4f();
                matrix4f.scale((float) (maxX-minX)+1, (float) (maxY-minY)+1,1F);

                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).setTransformationMatrix(matrix4f);
                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).teleport(new Location(world ,minX + ((maxX-minX)/2) +.5,minY + ((maxY-minY)/2) +.5,maxZ +.5,0,0));
            }
            id = 25;
            if (!faces.containsKey(id)) {
                spawnEntity(id,minX+0.5,minY + ((maxY-minY)/2) +.5,minZ + ((maxZ-minZ)/2) +.5,90,0,maxZ-minZ+1,maxY-minY+1,1,"blueprint_filler");
            } else {
                Matrix4f matrix4f = new Matrix4f();
                matrix4f.scale((float) (maxZ-minZ)+1, (float) (maxY-minY)+1,1F);

                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).setTransformationMatrix(matrix4f);
                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).teleport(new Location(world ,minX+0.5,minY + ((maxY-minY)/2) +.5,minZ + ((maxZ-minZ)/2) +.5,90,0));
            }

            id = 26;
            if (!faces.containsKey(id)) {
                spawnEntity(id,maxX+0.5,minY + ((maxY-minY)/2) +.5,minZ + ((maxZ-minZ)/2) +.5,-90,0,maxZ-minZ+1,maxY-minY+1,1,"blueprint_filler");
            } else {
                Matrix4f matrix4f = new Matrix4f();
                matrix4f.scale((float) (maxZ-minZ)+1, (float) (maxY-minY)+1,1F);

                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).setTransformationMatrix(matrix4f);
                ((ItemDisplay) Bukkit.getEntity(faces.get(id))).teleport(new Location(world ,maxX+0.5,minY + ((maxY-minY)/2) +.5,minZ + ((maxZ-minZ)/2) +.5,-90,0));
            }


        });
    }

    public void spawn() {
        spawnLines();
        spawnFaces();
    }
}
