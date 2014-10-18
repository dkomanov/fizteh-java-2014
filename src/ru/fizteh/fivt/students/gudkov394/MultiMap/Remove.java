package ru.fizteh.fivt.students.gudkov394.MultiMap;

public class Remove {
    public Remove(final String[] currentArgs, CurrentTable ct) {
        if (currentArgs.length != 2) {
            System.err.println("wrong number of argument to Remove");
            System.exit(1);
        }
        if (ct.remove(currentArgs[1]) != null) {
            System.out.println("removed");
        } else {
            System.out.println("not found");
        }
    }
}
