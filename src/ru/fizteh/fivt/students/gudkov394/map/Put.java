package ru.fizteh.fivt.students.gudkov394.map;

import java.util.Map;

public class Put {
    public Put(final String[] currentArgs, final Map ct) {
        if (currentArgs.length != 3) {
            System.err.println("wrong number of argument to Put");
            System.exit(1);
        }
        if (ct.containsKey(currentArgs[1])) {
            System.out.println("overwrite\n" + "old value = " + ct.get(currentArgs[1]));
        } else {
            System.out.println("new");
        }
        try {
            ct.put(currentArgs[1], currentArgs[2]);
        } catch (Throwable e) {
            System.err.println("problem with put");
            System.exit(2);
        }
        Write write = new Write(ct);
    }
}
