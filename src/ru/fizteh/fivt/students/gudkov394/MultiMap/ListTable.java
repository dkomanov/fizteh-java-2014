package ru.fizteh.fivt.students.gudkov394.MultiMap;

import java.util.Map;
import java.util.Set;

public class ListTable {
    public ListTable(final String[] currentArgs, Map<String, CurrentTable> ct) {
        Set<String> set = ct.keySet();
        for (String s : set) {
            System.out.println("name " + s + ", size " + ct.get(s).getNumber());
        }
        System.out.println();
    }
}
