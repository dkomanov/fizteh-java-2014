package ru.fizteh.fivt.students.ryad0m.multifile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DataBase {
    private Path location;
    private HashMap<String, Table> tables;

    public DataBase(Path path) {
        location = path;
    }

    public void load() throws IOException, BadFormatException {
        tables = new HashMap<String, Table>();
        if (!location.toFile().exists()) {
            location.toFile().mkdirs();
        }
        File[] tableDirs = location.toFile().listFiles();
        for (File table : tableDirs) {
            if (table.exists() && table.isDirectory()) {
                Table newTable = new Table(table.toPath());
                newTable.load();
                tables.put(table.getName(), newTable);
            }
        }
    }

    private void deleteDir(File dir) {
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteDir(f);
                } else {
                    f.delete();
                }
            }
        }
        dir.delete();
    }

    private void clearDir(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteDir(f);
                } else {
                    f.delete();
                }
            }
        }
    }

    public void save() throws IOException {
        clearDir(location.toFile());
        for (Map.Entry<String, Table> entry : tables.entrySet()) {
            entry.getValue().save();
        }
    }

    public void dropTable(String name) {
        tables.remove(name);
    }

    public Table getTable(String name) {
        return tables.get(name);
    }

    public void createTable(String name) throws IOException, BadFormatException {
        Table t = new Table(location.resolve(name).normalize());
        t.load();
        tables.put(name, t);
    }

    public boolean containTable(String name) {
        return tables.containsKey(name);
    }

    public Set<Map.Entry<String, Table>> getTables() {
        return tables.entrySet();
    }
}
