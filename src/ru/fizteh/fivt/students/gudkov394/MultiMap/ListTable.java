package ru.fizteh.fivt.students.gudkov394.MultiMap;

import java.util.Map;
import java.util.Set;

public class ListTable {
    public ListTable(final String[] currentArgs, CurrentTable ct) {
        Set<String> set = ct.keySet();
        for (String s : set) {
            System.out.print(s + ", ");
        }
        System.out.println();
    }
}
