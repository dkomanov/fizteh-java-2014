package ru.fizteh.fivt.students.gudkov394.Storable.src;

import java.util.Set;

public class ListTable {
    //   public ListTable(final String[] currentArgs, Map<String, CurrentTable> ct) {
    //     Set<String> set = ct.keySet();
    //  for (String s : set) {
    //    System.out.println("name " + s + ", size " + ct.get(s).getNumber());
    //}
    //System.out.println();
    //}
    public ListTable(final String[] currentArgs, final CurrentTable ct) {
        if (currentArgs.length != 1) {
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
