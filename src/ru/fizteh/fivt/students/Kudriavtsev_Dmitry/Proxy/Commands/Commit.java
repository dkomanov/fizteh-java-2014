package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Proxy.Commands;

import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Proxy.Welcome;

import java.io.PrintStream;

/**
 * Created by Дмитрий on 31.10.2014.
 */
public class Commit extends StoreableCommand {
    public Commit() {
        super("commit", 0);
    }

    @Override
    public  boolean exec(Welcome dbConnector, String[] args, PrintStream out, PrintStream err) {
        if (!checkArguments(args.length, err)) {
            return !batchModeInInteractive;
        }
        if (dbConnector.getActiveTable() == null) {
            if (batchModeInInteractive) {
                System.err.println("No table");
                return false;
            }
            noTable(err);
            return true;
        }
        out.println(dbConnector.getActiveTable().commit());
        return true;
    }
}
