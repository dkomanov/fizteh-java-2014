package ru.fizteh.fivt.students.AlexeyZhuravlev.JUnit;

import ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap.DataBaseDir;
import ru.fizteh.fivt.students.AlexeyZhuravlev.MultiFileHashMap.Table;

import java.util.HashMap;
import java.util.Map;

/**
 * @author AlexeyZhuravlev
 */
public class JUnitDataBaseDir {
    HashMap<String, HybridTable> tables;
    public DataBaseDir usualDbDir;

    public JUnitDataBaseDir(String path) throws Exception {
        usualDbDir = new DataBaseDir(path);
        tables = new HashMap<>();
        for (Map.Entry<String, Table> entry: usualDbDir.tables.entrySet()) {
            tables.put(entry.getKey(), new HybridTable(entry.getValue()));
        }
    }

    public HybridTable getUsing() {
        return tables.get(usualDbDir.using);
    }
}
