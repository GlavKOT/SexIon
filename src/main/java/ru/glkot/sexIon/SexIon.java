package ru.glkot.sexIon;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.glkot.sexIon.messaging.Receiver;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class SexIon {

    private static final Logger log = LoggerFactory.getLogger(SexIon.class);
    private transient final Server server = Bukkit.getServer();
    public UUID id;
    private static final Path nullFolder = Bukkit.getPluginsFolder().toPath().toAbsolutePath().getParent();
    private JSONObject data;

    public boolean generated() {
        return data.getBoolean("generated");
    }

    public JSONObject getData(){
        return data;
    }

    private Path SexIonFolder () {
        return Path.of(nullFolder.toString(),"SexIons");
    }

    private Path SexIonPath () {
        return Path.of(nullFolder.toString(),"SexIons",id.toString() + ".sex");
    }

    private File SexIonFile () {
        return SexIonPath().toFile();
    }

    public SexIon put (String key, Object object) {
        this.data.put(key,object);
        this.flush();
        return this;
    }
    public SexIon putUnFlush (String key, Object object) {
        this.data.put(key,object);
        return this;
    }

    public String toString(){
        return data.toString();
    }
    public String toString(int indentFactor){
        return data.toString(indentFactor);
    }

    public Object get (String key) {
        return this.data.get(key);
    }
    public String getString (String key) {
        return this.data.getString(key);
    }
    public int getInt (String key) {
        return this.data.getInt(key);
    }
    public float getFloat (String key) {
        return this.data.getFloat(key);
    }
    public JSONArray getJSONArray (String key) {
        return this.data.getJSONArray(key);
    }
    public JSONObject getJSONObject (String key) {
        return this.data.getJSONObject(key);
    }


    public void generate() {

        new Thread(() -> {
            WorldCreator wc = new WorldCreator(id.toString());
            wc.generator(new EmptyChunkGenerator());
            wc.createWorld();

            this.put("generated",true);
            this.flush();
        }).start();


    }

    public World world () {
        return Bukkit.getWorld(id.toString());
    }



    @Nullable
    public static SexIon get (UUID SexIonID) {
        File file1 = Path.of(nullFolder.toString(),"SexIons",SexIonID+".sex").toFile();
        if (file1.exists()) {
            return SexIon.fromFile(file1);
        }
        return null;

    }

    public SexIon (UUID SexIonID) {

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id",SexIonID.toString());

        SexIon sexIon = new SexIon(jsonObject);
        sexIon.put("generated",false);
        sexIon.put("%time",100);
        sexIon.generate();
        sexIon.flush();
    }


    private SexIon (JSONObject jsonObject) {
        this.id = UUID.fromString(jsonObject.getString("id"));
        this.data = jsonObject;
    }

    public void kill () {
            SexIonFile().delete();
            Bukkit.unloadWorld(id.toString(), true);
            new Thread(() -> {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    FileManager.deleteFile(Path.of(nullFolder.toString(), id.toString()).toFile());
                    FileManager.deleteFile(Path.of(nullFolder.toString(), id.toString()).toFile());
            }).start();


            JSONObject jsonObject = Sxlib.getCon();

            Receiver.sendAllSexions(jsonObject.getString("server-id"),jsonObject.getString("server-type"));
    }

    private void flush() {
        File file1 = Path.of(nullFolder.toString(),"SexIons",id.toString() + ".sex").toFile();

        Path.of(nullFolder.toString(),"SexIons").toFile().mkdirs();

        if (!file1.exists()) {
            try {
                file1.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file1);
            fileOutputStream.write(this.data.toString(4).getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JSONObject jsonObject = Sxlib.getCon();

        Receiver.sendAllSexions(jsonObject.getString("server-id"),jsonObject.getString("server-type"));

        //Receiver.receiveData("SexIons",SexIon.getAll());
    }

    public static SexIon fromFile (File file) {
        try {

            FileInputStream zov = new FileInputStream(file);
            JSONObject jsonObject = new JSONObject(new String(zov.readAllBytes()));
            return new SexIon(jsonObject);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<SexIon> getAll () {

        List<SexIon> output = new ArrayList<>();

        Path folder = Path.of(nullFolder.toString(),"SexIons");
        folder.toFile().mkdirs();
        for (File file1: folder.toFile().listFiles()) {
            output.add(SexIon.fromFile(file1));
        }

        return output;
    }
    public static int getCount () {
        Path folder = Path.of(nullFolder.toString(),"SexIons");
        if (Arrays.stream(folder.toFile().listFiles()).toList().isEmpty()) return 0;
        return folder.toFile().listFiles().length;
    }

    public static List<UUID> getAllIds () {

        List<UUID> output = new ArrayList<>();

        for (SexIon sexIon : SexIon.getAll()) {
            output.add(sexIon.id);
        }
        return output;
    }
    public static List<SexIon> getFreebies () {

        List<SexIon> i = new ArrayList<>();

        for (SexIon sexIon : SexIon.getAll()) {
            if (sexIon.data.getInt("%time") == 100) i.add(sexIon);
        }
        return i;
    }

    public void startMatch (int maxTimeSec) {
        this.put("maxTime",maxTimeSec);
        new Thread(() -> {
            for (double time = 0; time <= maxTimeSec; time=time+5) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                this.data.put("time",time);
                this.data.put("%time",(time/maxTimeSec)*100);
                this.flush();
            }
        }).start();
    }
}
