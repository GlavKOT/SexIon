package ru.glkot.sexIon;

import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

public class JsTest {

    public JSONObject object;

    public JsTest () {
        this.object = new JSONObject();
    }

    public void ItemsTest (ItemStack item, String name) {
        object.put(name,item);
    }
}
