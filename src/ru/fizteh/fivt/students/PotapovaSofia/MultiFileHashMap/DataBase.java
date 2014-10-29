package ru.fizteh.fivt.students.PotapovaSofia.MultiFileHashMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class DataBase {
    private Map<String, Table> tables;
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

    public Map<String, Table> getDataBase() {
        return tables;
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
                    tb.readFromTable();
                    tables.put(dir.getName(), tb);
                }
            }
        }
        currentTable = null;
    }

    public void writeToDataBase() throws IOException {
        for (Map.Entry<String, Table> entry : tables.entrySet()) {
            entry.getValue().writeToTable();
        }
    }
}
