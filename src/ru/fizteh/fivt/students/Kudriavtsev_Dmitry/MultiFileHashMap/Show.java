package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.MultiFileHashMap;

import java.util.Map;

/**
 * Created by Дмитрий on 07.10.14.
 */
public class Show extends Command {

    public Show() {
        super("show", 1);
    }

    @Override
    public boolean exec(Connector dbConnector, String[] args) {
        if (!checkArguments(args.length)) {
            if (packageModeInInteractive) {
                return false;
            }
            return true;
        }

        if (!args[0].equals("tables")) {
            System.err.println("Bad show tables command.");
            if (packageModeInInteractive) {
                return false;
            }
            if (packageMode) {
                System.exit(-1);
            }
            return true;
        }

        if (dbConnector.tables.isEmpty()) {
            return true;
        }

        for (Map.Entry<String, MFHMap> a : dbConnector.tables.entrySet()) {
            System.out.println(a.getKey() + " " + a.getValue().size());
        }

        return true;
    }
}
