package ru.fizteh.fivt.students.vladislav_korzun.MultiFileHashMap;

import java.nio.file.Path;
import java.nio.file.Paths;

public class MultiFileHashMap {
    public static void main(final String[] args) {
        try {
            Path dbdir = Paths.get(System.getProperty("fizteh.db.dir"));
            if (dbdir.toFile().exists()) {
                if (dbdir.toFile().isDirectory()) {
                    new Interpreter(args, dbdir);
                } else {   
                    throw new IllegalArgumentException();
                }
            } else {
                dbdir.toFile().mkdir();
                new Interpreter(args, dbdir);
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid path");
        } catch (Exception e) {
            System.err.println("Error");
        }
    }
}
