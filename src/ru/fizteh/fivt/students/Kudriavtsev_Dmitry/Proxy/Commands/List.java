package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Proxy.Commands;

import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Proxy.Welcome;

import java.io.PrintStream;

/**
 * Created by Дмитрий on 04.10.14.
 */
public class List extends StoreableCommand {
    public List() {
        super("list", 0);
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
        java.util.List<String> keySet = dbConnector.getActiveTable().list();
        int count = 0;
        for (String key : keySet) {
            out.print(key);
            if (count != keySet.size() - 1) {
                out.print(", ");
                ++count;
            }
        }
        out.println();
        return true;
    }
}
