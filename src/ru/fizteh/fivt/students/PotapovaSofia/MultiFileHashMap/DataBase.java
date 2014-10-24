package ru.fizteh.fivt.students.PotapovaSofia.MultiFileHashMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class DataBase {
    public Map<String, Table> tables;
    public Table currentTable;
    private Path dbPath;

    public DataBase(Path path) throws IOException {
        tables = new HashMap<>();
        dbPath = path;
        readFromDataBase();
    }

    public Path getDbPath() {
        return dbPath;
    }

    public void readFromDataBase() throws IOException {
        if (dbPath == null || !Files.exists(dbPath)) {
            throw new IOException("Wrong database path");
        }
        File currentDir = new File(dbPath.toString());
        File[] directories = currentDir.listFiles();
        if (directories != null) {
            for (File dir : directories) {
                if (Files.isDirectory(dir.toPath())) {
                    Table tb = new Table(dir.toPath());
                    try {
                        tb.readToTable();
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                        System.exit(1);
                    }
                    tables.put(dir.getName(), tb);
                }
            }
        }
        currentTable = null;
    }

    public void writeToDataBase() throws IOException {
        if (!Files.isDirectory(dbPath)) {
            Files.delete(dbPath);
        } else {
            deleteRecursive(dbPath);
        }
        for (Map.Entry<String, Table> entry : tables.entrySet()) {
            entry.getValue().writeFromTable();
        }
    }

    private void deleteRecursive(Path path) throws IOException {
        File currentDir = new File(path.toString());
        File[] directories = currentDir.listFiles();
        if (directories != null) {
            for (File dir : directories) {
                if (dir.isFile()) {
                    try {
                        Files.delete(dir.toPath());
                    } catch (IOException e) {
                        System.err.println(e.getMessage());
                        System.exit(1);
                    }
                } else {
                    deleteRecursive(dir.toPath());
                }
            }
        }
        if (!path.equals(dbPath)) {
            Files.delete(path);
        }
    }
}
