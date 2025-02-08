package ru.glkot.sexIon.menus;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import ru.glkot.sexIon.EmptyChunkGenerator;
import ru.glkot.sexIon.FileManager;
import ru.glkot.sexIon.Preset;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BackUP implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L; // ПОХУЙ НАДО
    private transient final Server server = Bukkit.getServer();
    public UUID id;
    private Preset map;
    private static final Path nullFolder = Bukkit.getPluginsFolder().toPath().toAbsolutePath().getParent();
    public boolean generated;


    private Path SexIonFolder () {
        return Path.of(nullFolder.toString(),"SexIons");
    }

    private Path SexIonPath () {
        return Path.of(nullFolder.toString(),"SexIons",id.toString() + ".sex");
    }

    private File SexIonFile () {
        return SexIonPath().toFile();
    }


    public World generate() {
        WorldCreator wc = new WorldCreator(id.toString());
        wc.generator(new EmptyChunkGenerator());

        World w = wc.createWorld();

        this.generated = true;
        this.flush();

        return w;
    }

    public World world () {
        return Bukkit.getWorld(id.toString());
    }



    @Nullable
    public static BackUP get (UUID SexIonID) {
        File file1 = Path.of(nullFolder.toString(),"SexIons",SexIonID+".sex").toFile();
        if (file1.exists()) {
            return BackUP.fromFile(file1);
        }
        return null;

    }

    public static BackUP create(UUID SexIonID) {
        BackUP sexIon = new BackUP(SexIonID);
        sexIon.generated = false;
        sexIon.flush();
        return sexIon;
    }


    public BackUP(UUID id) {
        this.id = id;
    }

    public void kill () {
        SexIonFile().delete();

        if (generated) {

            Bukkit.unloadWorld(id.toString(), true);

            FileManager.deleteFile(Path.of(nullFolder.toString(), id.toString()).toFile());
            FileManager.deleteFile(Path.of(nullFolder.toString(), id.toString()).toFile());
        }
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
            fileOutputStream.write(this.bytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // ЭТОТ ОБЪЕКТ В byte[]
    public byte[] bytes() {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(this);
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    // ЭТОТ ОБРАТНО


    public static BackUP byteArrayToObject(byte[] byteArray) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
             ObjectInputStream ois = new ObjectInputStream(bis)) {
            return (BackUP) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static BackUP fromFile(File file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            return byteArrayToObject(fileInputStream.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static List<BackUP> getAll () {

        List<BackUP> output = new ArrayList<>();

        Path folder = Path.of(nullFolder.toString(),"SexIons");
        folder.toFile().mkdirs();
        for (File file1: folder.toFile().listFiles()) {
            output.add(BackUP.fromFile(file1));
        }

        return output;
    }

    public static List<UUID> getAllIds () {

        List<UUID> output = new ArrayList<>();

        for (BackUP sexIon : BackUP.getAll()) {
            output.add(sexIon.id);
        }
        return output;
    }

}
