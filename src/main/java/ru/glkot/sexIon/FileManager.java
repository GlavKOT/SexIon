package ru.glkot.sexIon;

import java.io.File;

public class FileManager {
    public static void deleteFile(File file) {

        if (file.isDirectory()) {
            for (File c : file.listFiles())
                deleteFile(c);
            file.delete();
        } else {
            file.delete();
        }

    }
}
