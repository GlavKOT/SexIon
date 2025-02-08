package ru.glkot.sexIon.playerdata;

import org.bukkit.Bukkit;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.glkot.sexIon.messaging.Receiver;

import java.io.*;
import java.nio.file.Path;
import java.util.UUID;

public class PlayerData {

    private static final Logger log = LoggerFactory.getLogger(PlayerData.class);
    public UUID id;
    public JSONObject data;
    private final File file;

    public PlayerData(UUID id) {
        this.file = Path.of(Path.of("").toAbsolutePath().toString(), "PlayerData", id + ".player").toFile();
        this.id = id;


        if (file.exists()) {
            try {
                FileInputStream zov = new FileInputStream(file);
                this.data = new JSONObject(new String(zov.readAllBytes()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", id.toString());
            jsonObject.put("name", Bukkit.getOfflinePlayer(id).getName());
            this.data = jsonObject;
            flush();
        }
    }
    public PlayerData(JSONObject data) {
        this.data = data;
        this.id = UUID.fromString(data.getString("id"));
        this.file = Path.of(Path.of("").toAbsolutePath().toString(), "PlayerData", id + ".player").toFile();
        write();
    }

    public PlayerData put (String key,Object data){
        this.data.put(key,data);
        flush();
        return this;
    }

    public Object get (String key) {return this.data.get(key);}
    public String getString (String key) {return this.data.getString(key);}
    public int getInt (String key) {return this.data.getInt(key);}
    public float getFloat (String key) {return this.data.getFloat(key);}
    public JSONArray getJSONArray (String key) {return this.data.getJSONArray(key);}
    public JSONObject getJSONObject (String key) {return this.data.getJSONObject(key);}

    private void flush() {
        new Thread(() -> {
            Receiver.receiveData("playerData", data);
            write();
        }).start();
    }
    private void write(){
        try {
            Writer writer = new FileWriter(file);
            writer.write(data.toString(4));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            log.error(new RuntimeException(e).toString());
        }
    }
}
