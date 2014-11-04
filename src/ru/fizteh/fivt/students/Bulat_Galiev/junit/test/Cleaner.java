package ru.fizteh.fivt.students.Bulat_Galiev.junit.test;

import java.io.File;

public class Cleaner {
    public static void clean(File dir) {
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                clean(file);
            }
        }
        dir.delete();
    }
}
