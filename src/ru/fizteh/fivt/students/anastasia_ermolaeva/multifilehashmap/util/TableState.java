package ru.fizteh.fivt.students.anastasia_ermolaeva.multifilehashmap.util;

import ru.fizteh.fivt.students.anastasia_ermolaeva.multifilehashmap.Table;
import ru.fizteh.fivt.students.anastasia_ermolaeva.multifilehashmap.TableHolder;

import java.util.HashMap;
import java.util.Map;

public class TableState {
    private final Map<String,
            Map<String, String>> map;
    private String currentTableName = "";

    public TableState(final TableHolder tableHolder) {
        map = new HashMap<>();
        Map<String, Table> m = tableHolder.getTableMap();
        for (Map.Entry<String, Table> entry : m.entrySet()) {
            String key = entry.getKey();
            Map<String, String> value = entry.getValue().getAllRecords();
            map.put(key, value);
        }
    }

    public Map<String, Map<String, String>> getMap() {
        return map;
    }

    public String getCurrentTableName() {
        return currentTableName;
    }

    public void setCurrentTableName(String currentTableName) {
        this.currentTableName = currentTableName;
    }

    /*
    * False - no chosen table.
    */
    public final boolean checkCurrentTable() {
        boolean result = currentTableName.equals("");
        if (result) {
            System.out.println("no table");
        }
        return !result;
    }
}
