package ru.fizteh.fivt.students.standy66_new.utils;

import java.io.File;

/**
 * Created by andrew on 01.11.14.
 */
public class FileUtils {
    public static boolean deleteRecursively(File f) {
        if (f == null)
            return false;
        if (f.isDirectory()) {
            for (File sub : f.listFiles()) {
                if (sub.isDirectory()) {
                    if (!deleteRecursively(sub)) {
                        return false;
                    }
                } else if (!sub.delete()) {
                    return false;
                }
            }
        }
        return f.delete();
    }
}
