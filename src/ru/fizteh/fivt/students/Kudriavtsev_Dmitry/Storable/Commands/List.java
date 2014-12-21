package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Storable.Commands;

import ru.fizteh.fivt.students.Kudriavtsev_Dmitry.Storable.Connector;

/**
 * Created by Дмитрий on 04.10.14.
 */
public class List extends StoreableCommand {
    public List() {
        super("list", 0);
    }

    @Override
    public boolean exec(Connector dbConnector, String[] args) {
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
        java.util.List<String> keySet = dbConnector.getActiveTable().list();
        int count = 0;
        for (String key : keySet) {
            System.out.print(key);
            if (count != keySet.size() - 1) {
                System.out.print(", ");
                ++count;
            }
        }
        System.out.println();
        return true;
    }
}
