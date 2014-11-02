package ru.fizteh.fivt.students.anastasia_ermolaeva.junit.util;

import ru.fizteh.fivt.storage.strings.TableProvider;
import ru.fizteh.fivt.students.anastasia_ermolaeva.junit.DBTable;
import ru.fizteh.fivt.students.anastasia_ermolaeva.junit.TableHolder;

import java.util.HashMap;
import java.util.Map;

public class TableState {
    //private final Map<String,
      //      Map<String, String>> map;
    private String currentTableName = "";
    private TableProvider holder;

    public TableState(final TableProvider tableHolder) {
        holder = tableHolder;
        //map = new HashMap<>();
        //Map<String, DbTable> m = tableHolder.getTableMap();
        //for (Map.Entry<String, DbTable> entry : m.entrySet()) {
         //   String key = entry.getKey();
          //  Map<String, String> value = entry.getValue().getAllRecords();
          //  map.put(key, value);
        //}
    }
    public TableProvider getTableHolder(){
        return holder;
    }
    //public Map<String, Map<String, String>> getMap() {
       // return map;
    //}
    public String getCurrentTableName() {
        return currentTableName;
    }
    public void setCurrentTableName(String currentTableName) {
        this.currentTableName = currentTableName;
    }
}
