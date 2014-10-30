package ru.fizteh.fivt.students.Kudriavtsev_Dmitry.MultiFileHashMap;

import java.util.Set;

/**
 * Created by Дмитрий on 04.10.14.
 */
public class List extends Command {
    public List() {
        super("list", 0);
    }

    @Override
    public boolean exec(Connector dbConnector, String[] args) {
        if (!checkArguments(args.length)) {
            if (batchModeInInteractive) {
                return false;
            }
            return true;
        }
        if (dbConnector.activeTable == null) {
            if (batchModeInInteractive) {
                System.err.println("No table");
                return false;
            }
            noTable();
            return true;
        }
        Set<String> keySet = dbConnector.activeTable.keySet();
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
