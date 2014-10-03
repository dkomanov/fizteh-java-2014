package ru.fizteh.fivt.students.gudkov394.MultiMap;


import java.util.Map;


public class Exit {
    public Exit(final String[] currentArgs, CurrentTable currentTable) {
        if (currentArgs.length > 1) {
            System.err.println("extra arguments for exit");
            System.exit(1);
        }
        currentTable.write();
        System.exit(0);
    }
}
