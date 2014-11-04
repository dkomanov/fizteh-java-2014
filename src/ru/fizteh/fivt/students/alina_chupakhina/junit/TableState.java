package ru.fizteh.fivt.students.alina_chupakhina.junit;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by opa on 29.10.2014.
 */
public class TableState {
    public Map<String, String> fm;
    public int unsavedChangesCounter;
    public int numberOfElements;

    TableState(Map<String, String> FileMap, int u, int n) {
        fm = new TreeMap<String, String>(FileMap);
        unsavedChangesCounter = u;
        numberOfElements = n;
    }
}
