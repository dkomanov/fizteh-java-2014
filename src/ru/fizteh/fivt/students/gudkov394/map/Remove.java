package ru.fizteh.fivt.students.gudkov394.map;

import java.util.Map;

public class Remove {
    public Remove(final String[] currentArgs, final Map ct) {
        if (currentArgs.length != 2) {
            System.err.println("wrong number of argument to Get");
            System.exit(1);
        }
        if (ct.remove(currentArgs[1]) != null) {
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
    }
}
