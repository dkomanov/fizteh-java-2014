package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Proxy.Commands;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Proxy.CurrentStoreable;
import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Proxy.Welcome;

import java.io.PrintStream;

/**
 * Created by Дмитрий on 04.10.14.
 */
public class Get extends StoreableCommand {

    public Get() {
        super("get", 1);
    }

    @Override
    public  boolean exec(Welcome dbConnector, String[] args, PrintStream out, PrintStream err) {
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
        Storeable value = dbConnector.getActiveTable().get(args[0]);
        if (value != null) {
            out.println("found");
            for (Object val: ((CurrentStoreable) value).getValues()) {
                out.println(val.toString());
            }
        } else {
            err.println("not found");
            if (batchModeInInteractive) {
                return false;
            }
        }
        return true;
    }
}
