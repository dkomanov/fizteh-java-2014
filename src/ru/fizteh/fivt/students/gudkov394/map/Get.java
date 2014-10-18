package ru.fizteh.fivt.students.gudkov394.map;

import java.util.Map;

public class Get {
    public Get(final String[] currentArgs, final Map ct) {
        if (currentArgs.length != 2) {
            System.err.println("wrong number of argument to Get");
            System.exit(1);
        }
        if (ct.containsKey(currentArgs[1])) {
            System.out.println("Found\nvalue = " + ct.get(currentArgs[1]));
        } else {
            System.out.println("Not found");
        }
    }
}
