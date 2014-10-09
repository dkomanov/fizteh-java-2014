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
        if (args.length != argLen) {
            System.err.println("Incorrect number of arguments in " + name);
            return false;
        }
        Set<String> keySet = dbConnector.activeTable.keySet();
        for (String key : keySet) {
            System.out.print(key + " , ");
        }
        System.out.println();
        return true;
    }
}
