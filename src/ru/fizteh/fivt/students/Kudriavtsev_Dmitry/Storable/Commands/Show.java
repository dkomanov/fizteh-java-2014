package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Storable.Commands;

import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Storable.Connector;
import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Storable.StoreableTable;

import java.util.Map;

/**
 * Created by Дмитрий on 07.10.14.
 */
public class Show extends StoreableCommand {

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

        if (dbConnector.getTables().isEmpty() && dbConnector.getActiveTableProvider().tables.isEmpty()) {
            return true;
        }

        /*for (Map.Entry<String, StoreableTable> a : dbConnector.tables.entrySet()) {
            System.out.println(a.getKey() + " " + a.getValue().size());
        }*/

        for (Map.Entry<String, StoreableTable> a : dbConnector.getActiveTableProvider().tables.entrySet()) {
            System.out.println(a.getKey() + " " + a.getValue().size());
        }

        return true;
    }
}
