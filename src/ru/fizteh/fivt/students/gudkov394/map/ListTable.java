package ru.fizteh.fivt.students.gudkov394.map;

import java.util.Map;
import java.util.Set;

public class ListTable {
    public ListTable(final String[] currentArgs, final Map ct) {
        if(currentArgs.length != 1)
        {
            System.err.println("wrong number of argumets to list");
            System.exit(3);
        }
        Set<String> set = ct.keySet();
        for (String s : set) {
            System.out.print(s + ", ");
        }
        System.out.println();
    }
}
