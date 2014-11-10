package ru.fizteh.fivt.students.tonmit.MultiFileMap;

import java.util.Set;

public class List extends Command {
    public List() {
        this.name = "list";
        this.argLen = 0;
    }

    public boolean exec(Connector dbConnector, String[] args) {
        if (!checkArguments(args.length)) {
            return false;
        }
        if (dbConnector.currentTable == null) {
            noTable();
            return false;
        }
        Set<String> keySet = dbConnector.currentTable.keySet();
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
