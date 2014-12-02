package ru.fizteh.fivt.students.ryad0m.junit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MyTableProvider implements TableProvider {
    private Path location;
    private HashMap<String, MyTable> tables;

    public MyTableProvider(Path path) {
        location = path;
        tables = new HashMap<String, MyTable>();
        if (!location.toFile().exists()) {
            location.toFile().mkdirs();
        }
        if (!location.toFile().isDirectory()) {
            throw new IllegalArgumentException();
        }
        File[] tableDirs = location.toFile().listFiles();
        for (File table : tableDirs) {
            if (table.exists() && table.isDirectory()) {
                MyTable newMyTable = new MyTable(table.toPath());
                tables.put(table.getName(), newMyTable);
            }
        }
    }

    private boolean checkName(String s) {
        if (s == null) {
            return false;
        }
        return !s.contains("/");
    }

    public Set<Map.Entry<String, MyTable>> getTables() {
        return tables.entrySet();
    }

    @Override
    public Table getTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        if (tables.containsKey(name)) {
            return new UserTable(tables.get(name));
        } else {
            return null;
        }
    }

    @Override
    public Table createTable(String name) {
        if (!checkName(name)) {
            throw new IllegalArgumentException();
        }
        if (tables.containsKey(name)) {
            return null;
        }
        MyTable t = new MyTable(location.resolve(name).normalize());
        tables.put(name, t);
        return new UserTable(t);
    }

    public MyTable getTables(String name) {
        return tables.get(name);
    }

    @Override
    public void removeTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        if (!tables.containsKey(name)) {
            throw new IllegalStateException();
        }
        tables.get(name).deleteData();
        tables.remove(name);
    }
}
