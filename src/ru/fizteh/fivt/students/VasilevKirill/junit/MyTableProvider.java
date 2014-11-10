package ru.fizteh.fivt.students.VasilevKirill.junit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.VasilevKirill.junit.multimap.MultiMap;
import ru.fizteh.fivt.students.VasilevKirill.junit.multimap.MultiTable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kirill on 10.11.2014.
 */
public class MyTableProvider implements TableProvider {
    private MultiMap tables;
    Map<String, Table> tableMap;

    public MyTableProvider(String workingDirectory) {
        try {
            tables = new MultiMap(workingDirectory);
            tableMap = new HashMap<>();
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public Table createTable(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        try {
            if (!tables.addTable(name)) {
                return null;
            }
            MultiTable thisMultiTable = tables.getMultiTable(name);
            if (thisMultiTable == null) {
                throw new IOException("Unknown error");
            }
            Table retTable = new MyTable(thisMultiTable);
            tableMap.put(name, retTable);
            return retTable;
        } catch (IOException e) {
            if (e.getMessage().substring(0, 5).equals("Can't")) {
                throw new IllegalArgumentException();
            }
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public Table getTable(String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        return tableMap.get(name);
    }

    @Override
    public void removeTable(String name) throws IllegalArgumentException, IllegalStateException {
        if (name == null) {
            throw new IllegalArgumentException();
        }
        if (tableMap.get(name) == null) {
            throw new IllegalStateException();
        }
        try {
            tables.removeTable(name);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
