package ru.fizteh.fivt.students.deserg.multifilehashmap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

/**
 * Created by deserg on 20.10.14.
 */
public class Database {

    public HashMap<String, Table> tables;
    public Table curTable;
    private Path dbPath;

    public Path getDbPath() {
        return dbPath;
    }


    public Database(Path inpPath) {
        tables = new HashMap<>();
        dbPath = inpPath;
        read();
    }



    public void read() {

        if (dbPath == null || !Files.exists(dbPath)) {
            throw new MyException("Wrong database path");
        }

        File curDir = new File(dbPath.toString());
        File[] content = curDir.listFiles();

        if (content != null) {
            for (File item: content) {
                if (Files.isDirectory(item.toPath())) {

                    Table table = new Table(item.toPath());
                    try {
                        table.read();
                    } catch (MyIOException ex) {
                        System.out.println(ex.getMessage());
                        System.exit(1);
                    }
                    tables.put(item.getName(), table);
                }
            }
        }

        curTable = null;
    }

    public void write() {

        deleteDirectoryContent();

        for (HashMap.Entry<String, Table> entry: tables.entrySet()) {

            try {
                entry.getValue().write();
            } catch (MyIOException ex) {
                System.out.println(ex.getMessage());
                System.exit(1);
            }

        }

    }

    public void deleteDirectoryContent() {

        if (!Files.isDirectory(dbPath)) {
            try {
                Files.delete(dbPath);
            } catch (IOException ex) {
                throw new MyException("Error while deleting " + dbPath.toString());
            }
        } else {
            deleteFinal(dbPath);
        }

    }

    private void deleteFinal(Path path) {

        File curDir = new File(path.toString());
        File[] content = curDir.listFiles();

        if (content != null) {
            for (File item: content) {
                if (item.isFile()) {
                    try {
                        Files.delete(item.toPath());
                    } catch (IOException ex) {
                        throw new MyException("I/O error occurs while removing " + item.toPath().toString());
                    }
                } else {
                    deleteFinal(item.toPath());
                }
            }
        }

        if (!path.equals(dbPath)) {
            try {
                Files.delete(path);
            } catch (IOException ex) {
                throw new MyException("I/O error occurs while removing " + path.toString());
            }
        }

    }


}
