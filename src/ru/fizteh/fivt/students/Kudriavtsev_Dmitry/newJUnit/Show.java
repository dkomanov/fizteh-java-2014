package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.newJUnit;

import java.util.Map;

/**
 * Created by Дмитрий on 07.10.14.
 */
public class Show extends JUnitCommand {

    public Show() {
        super("show", 1);
    }

    private boolean checkForArgs() {
        System.err.println("Bad show tables command.");
        if (batchModeInInteractive) {
            return false;
        }
        if (batchMode) {
            System.exit(-1);
        }
        return true;
    }

    @Override
    public boolean exec(Connector dbConnector, String[] args) {

        if (args.length == 0) {
            return checkForArgs();
        }

        if (!args[0].equals("tables") || args.length > 1) {
            return checkForArgs();
        }

        if (dbConnector.tables.isEmpty() && dbConnector.activeTableProvider.tables.isEmpty()) {
            return true;
        }

        for (Map.Entry<String, CurrentTable> a : dbConnector.tables.entrySet()) {
            System.out.println(a.getKey() + " " + a.getValue().size());
        }

        for (Map.Entry<String, CurrentTable> a : dbConnector.activeTableProvider.tables.entrySet()) {
            System.out.println(a.getKey() + " " + a.getValue().size());
        }

        return true;
    }
}
