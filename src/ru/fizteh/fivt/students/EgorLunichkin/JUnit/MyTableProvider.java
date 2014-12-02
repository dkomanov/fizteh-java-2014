package ru.fizteh.fivt.students.EgorLunichkin.JUnit;

import ru.fizteh.fivt.storage.strings.*;
import ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap.*;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

public class MyTableProvider implements TableProvider {
    public MyTableProvider(String dbDir) {
        using = null;
        try {
            multiDataBase = new MultiDataBase(dbDir);
        } catch (Exception ex) {
            throw new IllegalArgumentException();
        }
        tables = new HashMap<>();
        for (Map.Entry<String, MultiTable> entry : multiDataBase.tables.entrySet()) {
            tables.put(entry.getKey(), new MyTable(entry.getValue(), entry.getKey()));
        }
    }

    public HashMap<String, Table> tables;
    public MultiDataBase multiDataBase;
    public String using;

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
            PrintStream normal = System.out;
            System.setOut(new PrintStream(new NoWriteOutputStream()));
            Command command = new CreateCommand(this.multiDataBase, name);
            try {
                command.run();
            } catch (Exception ex) {
                throw new IllegalArgumentException();
            }
            tables.put(name, new MyTable(multiDataBase.tables.get(name), name));
            System.setOut(normal);
            return this.getTable(name);
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
        PrintStream normal = System.out;
        System.setOut(new PrintStream(new NoWriteOutputStream()));
        Command drop = new DropCommand(this.multiDataBase, name);
        try {
            drop.run();
        } catch (Exception ex) {
            throw new IllegalArgumentException();
        } finally {
            System.setOut(normal);
        }
        tables.remove(name);
    }

    public Table getUsing() {
        return tables.get(using);
    }
}
