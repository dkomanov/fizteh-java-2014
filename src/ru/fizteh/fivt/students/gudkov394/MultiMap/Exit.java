package ru.fizteh.fivt.students.gudkov394.MultiMap;


public class Exit {
    public Exit(final String[] currentArgs) {
        if (currentArgs.length > 1) {
            System.err.println("extra arguments for exit");
            System.exit(1);
        }
        System.exit(0);
    }
}
