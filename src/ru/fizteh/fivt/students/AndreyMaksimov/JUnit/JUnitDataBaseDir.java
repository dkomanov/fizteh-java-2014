package ru.fizteh.fivt.students.MaksimovAndrey.JUnit;

import ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit.DataBaseDir;
import ru.fizteh.fivt.students.MaksimovAndrey.JUnit.MultiFileMapForJUnit.Table;

import java.util.HashMap;
import java.util.Map;

public class JUnitDataBaseDir {
    public HashMap<String, HybridTable> tables;
    public DataBaseDir usualDbDir;

    public JUnitDataBaseDir(String path) throws Exception {
        usualDbDir = new DataBaseDir(path);
        tables = new HashMap<>();
        for (Map.Entry<String, Table> entry: usualDbDir.tables.entrySet()) {
            tables.put(entry.getKey(), new HybridTable(entry.getValue()));
        }
    }

    public JUnitDataBaseDir() {}

    public HybridTable getUsing() {
        return tables.get(usualDbDir.using);
    }
}
