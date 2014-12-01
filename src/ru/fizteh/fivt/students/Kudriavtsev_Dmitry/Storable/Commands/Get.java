package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Storable.Commands;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Storable.Connector;
import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Storable.CurrentStoreable;

/**
 * Created by Дмитрий on 04.10.14.
 */
public class Get extends StoreableCommand {

    public Get() {
        super("get", 1);
    }

    @Override
    public  boolean exec(Connector dbConnector, String[] args) {
        if (!checkArguments(args.length)) {
            return !batchModeInInteractive;
        }
        if (dbConnector.activeTable == null) {
            if (batchModeInInteractive) {
                System.err.println("No table");
                return false;
            }
            noTable();
            return true;
        }
        Storeable value = dbConnector.activeTable.get(args[0]);
        if (value != null) {
            System.out.println("found");
            for (Object val: ((CurrentStoreable) value).values) {
                System.out.println(val.toString());
            }
        } else {
            System.err.println("not found");
            if (batchModeInInteractive) {
                return false;
            }
        }
        return true;
    }
}
