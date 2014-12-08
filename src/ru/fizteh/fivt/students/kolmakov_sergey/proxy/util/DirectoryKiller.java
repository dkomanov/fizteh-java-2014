package ru.fizteh.fivt.students.kolmakov_sergey.proxy.util;

import java.io.File;

/**
 * Created by Sergey on 08.12.2014.
 */
public final class DirectoryKiller {
    private DirectoryKiller() {
        // Mustn't be created.
    }
    public static void delete(File directory) {
        if (directory.isDirectory()) {
            try {
                for (File currentFile : directory.listFiles()) {
                    delete(currentFile);
                }
            } catch (NullPointerException e) {
                System.out.println("Error while recursive deleting directory.");
            }
            directory.delete();
        } else {
            directory.delete();
        }
    }
}
