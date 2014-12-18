package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Parallel.Commands;

import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Parallel.Connector;

/**
 * Created by Дмитрий on 31.10.2014.
 */
public class Commit extends StoreableCommand {
    public Commit() {
        super("commit", 0);
    }

    @Override
    public  boolean exec(Connector dbConnector, String[] args) {
        if (!checkArguments(args.length)) {
            return !batchModeInInteractive;
        }
        if (dbConnector.getActiveTable() == null) {
            if (batchModeInInteractive) {
                System.err.println("No table");
                return false;
            }
            noTable();
            return true;
        }
        System.out.println(dbConnector.getActiveTable().commit());
        return true;
    }
}
