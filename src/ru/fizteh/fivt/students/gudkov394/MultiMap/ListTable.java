package ru.fizteh.fivt.students.gudkov394.MultiMap;

import java.util.Set;

public class ListTable {
    public ListTable(final String[] currentArgs, CurrentTable ct) {
        Set<String> set = ct.keySet();
        for (String s : set) {
            System.out.println("name " + s + ", size " + ct.getNumber());
        }
        System.out.println();
    }
}
