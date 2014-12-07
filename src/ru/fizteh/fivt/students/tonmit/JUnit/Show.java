package ru.fizteh.fivt.students.tonmit.JUnit;

import java.util.Map;

public class Show extends JUnitCommand {

    public Show() {
        super("show", 1);
    }

    @Override
    public boolean exec(Connector dbConnector, String[] args) {
        if (!checkArguments(args.length)) {
            return !batchModeInInteractive;
        }

        if (!args[0].equals("tables")) {
            System.err.println("Bad show tables command.");
            if (batchModeInInteractive) {
                return false;
            }
            if (batchMode) {
                System.exit(-1);
            }
            return true;
        }

        if (dbConnector.tables.isEmpty()) {
            return true;
        }

        for (Map.Entry<String, CurrentTable> a : dbConnector.tables.entrySet()) {
            System.out.println(a.getKey() + " " + a.getValue().size());
        }

        return true;
    }
}
