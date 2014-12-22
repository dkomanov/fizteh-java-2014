package ru.fizteh.fivt.students.Bulat_Galiev.junit.test;

import java.io.File;

public final class Cleaner {
    private Cleaner() {
        // Unused constructor for checkstyle
    }

    public static void clean(final File dir) {
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                clean(file);
            }
        }
        dir.delete();
    }
}
