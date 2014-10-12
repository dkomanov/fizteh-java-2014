/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.filemap.table;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 *
 * @author shakarim
 */
public abstract class AbstractTable implements Table {

    protected HashMap<String, String> table = new HashMap<>();

    public String put(String key, String value) {
        return table.put(key, value);
    }

    public String get(String key) {
        return table.get(key);
    }

    public String[] list() {
        String[] retVal = new String[table.size()];
        int index = 0;
        for (Entry<String, String> i : table.entrySet()) {
            retVal[index++] = i.getKey();
        }
        return retVal;
    }

    public String remove(String key) {
        return table.remove(key);
    }
}
