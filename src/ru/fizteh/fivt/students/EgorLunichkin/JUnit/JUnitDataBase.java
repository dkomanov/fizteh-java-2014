package ru.fizteh.fivt.students.EgorLunichkin.JUnit;

import ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap.MultiDataBase;
import ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap.Table;

import java.util.HashMap;

public class JUnitDataBase {
    public JUnitDataBase(String dbDir) throws Exception {
        multiDataBase = new MultiDataBase(dbDir);
        tables = new HashMap<String, HybridTable>();
        for (HashMap.Entry<String, Table> entry : multiDataBase.tables.entrySet()) {
            tables.put(entry.getKey(), new HybridTable(entry.getValue()));
        }
    }

    public MultiDataBase multiDataBase;
    public HashMap<String, HybridTable> tables;

    public HybridTable getUsing() {
        return tables.get(multiDataBase.using);
    }
}
