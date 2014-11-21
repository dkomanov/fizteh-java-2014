package ru.fizteh.fivt.students.standy66_new.utility;

import java.io.File;

/**
 * Created by andrew on 01.11.14.
 */
public final class FileUtility {
    private FileUtility() {
    }

    public static boolean deleteRecursively(File file) {
        if (file == null) {
            return false;
        }
        if (file.isDirectory()) {
            //noinspection ConstantConditions
            for (File sub : file.listFiles()) {
                if (sub.isDirectory()) {
                    if (!deleteRecursively(sub)) {
                        return false;
                    }
                } else if (!sub.delete()) {
                    return false;
                }
            }
        }
        return file.delete();
    }
}
