package ru.fizteh.fivt.students.kuzmichevdima.FileHashMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class DB {
    private Map<String, Table> tables;
    public Table currentTable;
    private Path dbPath;

    public DB(Path path) throws IOException {
        tables = new HashMap<String, Table>();
        dbPath = path;
        readDataBase();
    }

    public Path getPath() {
        return dbPath;
    }

    public Map<String, Table> getDataBase() {
        return tables;
    }

    public void readDataBase() throws IOException {
        if (dbPath == null || !Files.exists(dbPath)) {
            throw new IOException("Wrong database path");
        }
        File currentDir = new File(dbPath.toString());
        File[] directories = currentDir.listFiles();
        if (directories != null) {
            for (File dir : directories) {
                if (Files.isDirectory(dir.toPath())) {
                    Table table = new Table(dir.toPath());
                    table.readFromTable();
                    tables.put(dir.getName(), table);
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
