package ru.glkot.sexIon.menus;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.glkot.sexIon.SexIon;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static ru.glkot.sexIon.CommandListener.sxOutText;

public class ScrollinListener implements Listener {

    List<ItemStack> sexlist () {
        List<ItemStack> items = new ArrayList<>();

        for (SexIon sexIon : SexIon.getAll()) {
            ItemStack item = new ItemStack(Material.GRASS_BLOCK);
            ItemMeta meta = item.getItemMeta();
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("sexion"));
            lore.add(Component.text(sexIon.id.toString()));
            meta.lore(lore);
            meta.displayName(Component.text(sexIon.id.toString()));
            item.setItemMeta(meta);
            items.add(item);
        }

        items.add(AdminMenu.IT(Material.NETHER_STAR,"NEW","add-sexion","null"));
        return items;
    }


    @EventHandler
    void huy(InventoryClickEvent e) {
        if (e.getCurrentItem() != null && (e.getCurrentItem().equals(Buttons.left()) || e.getCurrentItem().equals(Buttons.right()))) {
            int id = e.getView().getTopInventory().getItem(0).getAmount();
            String uid = PlainTextComponentSerializer.plainText().serialize(e.getView().getTopInventory().getItem(0).displayName()).replaceAll("\\[","").replaceAll("]","");
            Scrollin scrollin = Scrollin.get(uid);
            Bukkit.getServer().sendMessage(Component.text(uid));

            if (e.getSlot() == 48 && id >0) e.getWhoClicked().openInventory(scrollin.page(id - 1));
            if (e.getSlot() == 50 && id < scrollin.max -1) e.getWhoClicked().openInventory(scrollin.page(id + 1));
        }

        if (e.getCurrentItem() != null && e.getSlot() > 8 && e.getSlot() < 45) {
            String action = "";
            String args = "";
            if (e.getCurrentItem().lore() != null) action = PlainTextComponentSerializer.plainText().serialize( e.getCurrentItem().getItemMeta().lore().get(0));
            if (e.getCurrentItem().lore() != null) args = PlainTextComponentSerializer.plainText().serialize( e.getCurrentItem().getItemMeta().lore().get(1));

            switch (action) {
                case "open-list" -> {
                    switch (args) {
                        case "sexion" -> {

                            e.getWhoClicked().sendMessage(Component.text("Open sexions"));
                            Scrollin scrollin = new Scrollin(sexlist());

                            e.getWhoClicked().openInventory(scrollin.page(0));
                        }
                        case "models" -> {
                            List<ItemStack> itemStacks = new ArrayList<>();
                            try (FileInputStream zov = new FileInputStream("models.txt")) {
                                String s = new String(zov.readAllBytes());
                                for (String string : s.replace("[","").replace("]","").split(", ")) {
                                    ItemStack itemStack = new ItemStack(Material.PAPER);
                                    ItemMeta meta = itemStack.getItemMeta();
                                    meta.setItemModel(NamespacedKey.fromString(string));
                                    meta.displayName(Component.text(string));
                                    List<Component> lore = new ArrayList<>();
                                    lore.add(Component.text("giveItem"));
                                    lore.add(Component.text("giveItem"));
                                    meta.lore(lore);
                                    itemStack.setItemMeta(meta);
                                    itemStacks.add(itemStack);
                                }
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }

                            e.getWhoClicked().sendMessage(Component.text("Open models"));
                            Scrollin scrollin = new Scrollin(itemStacks);

                            e.getWhoClicked().openInventory(scrollin.page(0));
                        }
                        case "buttons" -> {
                            List<ItemStack> itemStacks = new ArrayList<>();
                            try (FileInputStream zov = new FileInputStream("buttons.txt")) {
                                String s = new String(zov.readAllBytes());
                                for (String string : s.replace("[","").replace("]","").split(", ")) {
                                    ItemStack itemStack = new ItemStack(Material.PAPER);
                                    ItemMeta meta = itemStack.getItemMeta();
                                    meta.setItemModel(NamespacedKey.fromString(string));
                                    meta.displayName(Component.text(string));
                                    List<Component> lore = new ArrayList<>();
                                    lore.add(Component.text("giveItem"));
                                    lore.add(Component.text("giveItem"));
                                    meta.lore(lore);
                                    itemStack.setItemMeta(meta);
                                    itemStacks.add(itemStack);
                                }
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }

                            e.getWhoClicked().sendMessage(Component.text("Open buttons"));
                            Scrollin scrollin = new Scrollin(itemStacks);

                            e.getWhoClicked().openInventory(scrollin.page(0));
                        }
                    }
                }
                case "add-sexion" -> {

                    UUID uuid = UUID.randomUUID();


                    e.getWhoClicked().sendMessage("\n");
                    e.getWhoClicked().sendMessage(sxOutText("Created new session"));
                    e.getWhoClicked().sendMessage(sxOutText("COPY ID BY CLICK").clickEvent(ClickEvent.copyToClipboard(uuid.toString())));
                    e.getWhoClicked().sendMessage("\n");

                    SexIon sexIon = new SexIon(uuid);

                    e.getWhoClicked().sendMessage(Component.text("Open sexions"));


                    Scrollin scrollin = new Scrollin(sexlist());

                    e.getWhoClicked().openInventory(scrollin.page(0));
                }
                case "sexion" -> {
                    List<ItemStack> itemStacks = new ArrayList<>();
                    itemStacks.add(AdminMenu.IT(Material.RED_DYE,"DELETE ","delete-sexion",args));
                    itemStacks.add(AdminMenu.IT(Material.BLAST_FURNACE,"GENERATE","gen-sexion",args));
                    if (SexIon.get(UUID.fromString(args)).generated()) itemStacks.add(AdminMenu.IT(Material.ENDER_PEARL,"TELEPORT","tp-sexion",args));
                    itemStacks.add(AdminMenu.IT(Material.ARMADILLO_SCUTE,"START MATCH","start-match",args));
                    Scrollin scrollin = new Scrollin(itemStacks);
                    e.getWhoClicked().openInventory(scrollin.page(0));
                }
                case "delete-sexion" -> {
                    SexIon.get(UUID.fromString(args)).kill();
                    Scrollin scrollin = new Scrollin(sexlist());
                    e.getWhoClicked().openInventory(scrollin.page(0));
                }
                case "gen-sexion" -> {
                    List<ItemStack> itemStacks = new ArrayList<>();
                    itemStacks.add(AdminMenu.IT(Material.RED_DYE,"DELETE ","delete-sexion",args));
                    itemStacks.add(AdminMenu.IT(Material.BLAST_FURNACE,"GENERATE","gen-sexion",args));
                    if (SexIon.get(UUID.fromString(args)).generated()) itemStacks.add(AdminMenu.IT(Material.ENDER_PEARL,"TELEPORT","tp-sexion",args));
                    itemStacks.add(AdminMenu.IT(Material.ARMADILLO_SCUTE,"START MATCH","start-match",args));
                    Scrollin scrollin = new Scrollin(itemStacks);
                    e.getWhoClicked().openInventory(scrollin.page(0));
                    if (!SexIon.get(UUID.fromString(args)).generated()) SexIon.get(UUID.fromString(args)).generate();

                }

                case "tp-sexion" -> {
                    e.getWhoClicked().teleport(new Location(SexIon.get(UUID.fromString(args)).world(), 0, 100, 0));
                    e.getWhoClicked().closeInventory();
                }
                case "start-match" -> {
                    SexIon.get(UUID.fromString(args)).startMatch(60);
                    e.getWhoClicked().closeInventory();
                }
                case "giveItem" -> {
                    ItemStack itemStack = e.getCurrentItem().clone();
                    itemStack.lore(new ArrayList<>());
                    if (e.getClick().isShiftClick()) {
                        e.getWhoClicked().setItemOnCursor(itemStack.asQuantity(64));
                    } else {
                        if (!e.getCursor().isEmpty()) {
                            if (e.getCursor().getItemMeta().getItemModel().equals(itemStack.getItemMeta().getItemModel())) {
                                e.getWhoClicked().setItemOnCursor(itemStack.asQuantity(e.getCursor().getAmount()+1));
                                e.getWhoClicked().sendMessage("adwawdaw");
                            } else {
                                e.getWhoClicked().setItemOnCursor(itemStack);
                            }
                        } else {
                            e.getWhoClicked().setItemOnCursor(itemStack);
                        }
                    }

                }
            }
        }


        if ((e.getClick().isShiftClick() || e.getClickedInventory().getType() != InventoryType.PLAYER) && e.getView().getTitle().contains("\uE132")) {
            e.setCancelled(true);
            if (e.getSlot() == 0) e.getWhoClicked().closeInventory();
        }
    }
}
