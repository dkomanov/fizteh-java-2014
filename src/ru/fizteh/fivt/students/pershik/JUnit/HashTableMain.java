package ru.fizteh.fivt.students.pershik.JUnit;

import ru.fizteh.fivt.students.pershik.FileMap.Runner;

import java.io.File;

/**
 * Created by pershik on 10/29/14.
 */
public class HashTableMain {
    public static void main(String[] args) {
        try {
            String dbDirPath = System.getProperty("fizteh.db.dir");
            File dbDir = new File(dbDirPath);
            if (dbDir.exists() && !dbDir.isDirectory()) {
                System.err.println(dbDir + " is not a directory");
                System.exit(-1);
            }
            if (!dbDir.exists()) {
                if (!dbDir.mkdirs()) {
                    System.err.println("Can't create " + dbDirPath);
                    System.exit(-1);
                }
            }
            Runner db = new HashTableRunner(dbDir.getAbsolutePath());
            if (args.length == 0) {
                db.runInteractive();
            } else {
                db.runBatch(args);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }
}

