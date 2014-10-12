package ru.fizteh.fivt.students.semenenko_denis.FileMap;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by denny_000 on 08.10.2014.
 */

public class DataBaseCache {
    TableFileDAT table = new TableFileDAT();

    public void init(String dbpath) {
        try {
            Path dbFilePath = Paths.get(System.getProperty("db.file"));
            try {
                RandomAccessFile dbFile
                        = new RandomAccessFile(dbFilePath.toString(), "rw");
                table.setBinFile(dbFile);
                if (dbFile.length() > 0) {
                    table.read(dbFile);
                }

            } catch (FileNotFoundException e) {
                dbFilePath.toFile().createNewFile();
                RandomAccessFile dbFile
                        = new RandomAccessFile(dbFilePath.toString(), "r");
                table.setBinFile(dbFile);
            }
        } catch (IOException e) {
            System.err.println("Error connecting database.");
            System.err.println("Reason: " + e.getMessage());
            System.exit(-1);
        }
    }

    public void commit() {
        table.write(table.getBinFile());
    }

    public void put(String key, String value, String table) {
        this.table.put(key, value);
    }

    public void get(String key) {
        table.get(key);
    }

    public void list(String table) {
        this.table.list();
    }

    public void remove(String key) {
        table.remove(key);
    }
}
