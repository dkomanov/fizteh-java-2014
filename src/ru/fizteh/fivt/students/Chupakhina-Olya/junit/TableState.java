package ru.fizteh.fivt.students.olga_chupakhina.junit;

import java.util.Map;
import java.util.TreeMap;

public class TableState {
    public Map<String, String> map;
    public int unsavedChanges;
    public int numberOfElements;

    TableState(Map<String, String> map, int uc, int noe) {
        map = new TreeMap<String, String>(map);
        unsavedChanges = uc;
        numberOfElements = noe;
    }
}
