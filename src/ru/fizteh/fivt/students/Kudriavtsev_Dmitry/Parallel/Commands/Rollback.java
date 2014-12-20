package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Parallel.Commands;

import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Parallel.Welcome;

import java.io.PrintStream;

/**
 * Created by Дмитрий on 31.10.2014.
 */
public class Rollback extends StoreableCommand {
    public Rollback() {
        super("rollback", 0);
    }

    @Override
    public boolean exec(Welcome dbConnector, String[] args, PrintStream out, PrintStream err) {
        if (!checkArguments(args.length, err)) {
            return !batchModeInInteractive;
        }
        if (dbConnector.getActiveTable() == null) {
            if (batchModeInInteractive) {
                err.println("No table");
                return false;
            }
            noTable(err);
            return true;
        }
        out.println(dbConnector.getActiveTable().rollback());
        return true;
    }
}
