package ru.fizteh.fivt.students.gudkov394.map;


import java.util.Map;


public class Exit {
    public Exit(final String[] currentArgs, final Map currentTable) {
        if (currentArgs.length > 1) {
            System.err.println("extra arguments for exit");
            System.exit(1);
        }
        Write write = new Write(currentTable);
        System.exit(0);
    }
}
