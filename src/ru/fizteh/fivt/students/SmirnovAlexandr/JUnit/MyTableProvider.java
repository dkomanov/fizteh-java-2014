package ru.fizteh.fivt.students.SmirnovAlexandr.JUnit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.SmirnovAlexandr.JUnit.MultiFileHashMap.*;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class MyTableProvider implements TableProvider {

    HashMap<String, Table> tables;
    DataBaseDir usualDbDir;
    String using;

    protected MyTableProvider(String path) {
        try {
            using = null;
            usualDbDir = new DataBaseDir(path);
            tables = new HashMap<>();
            for (Map.Entry<String, MultiTable> entry: usualDbDir.tables.entrySet()) {
                tables.put(entry.getKey(), new MyTable(entry.getValue(), entry.getKey()));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public Table getTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        if (tables.containsKey(name)) {
            return tables.get(name);
        } else {
            return null;
        }
    }

    @Override
    public Table createTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        if (tables.containsKey(name)) {
            return null;
        } else {
            PrintStream out = System.out;
            System.setOut(new PrintStream(new NoWriteOutputStream()));
            try {
                Command command = new Create(name);
                command.execute(usualDbDir);
                tables.put(name, new MyTable(usualDbDir.tables.get(name), name));
            } catch (Exception e) {
                throw new IllegalArgumentException();
            }
            System.setOut(out);
            return getTable(name);
        }
    }

    @Override
    public void removeTable(String name) {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        if (!tables.containsKey(name)) {
            throw new IllegalStateException();
        }
        PrintStream out = System.out;
        System.setOut(new PrintStream(new NoWriteOutputStream()));
        try {
            Command drop = new Drop(name);
            drop.execute(usualDbDir);
            tables.remove(name);
        } catch (Exception e) {
            throw new IllegalArgumentException();
        } finally {
            System.setOut(out);
        }
    }

    public Table getUsing() {
        return tables.get(using);
    }

    public void setUsing(String passed) {
        using = passed;
    }

    public DataBaseDir getUsual() {
        return usualDbDir;
    }
}
