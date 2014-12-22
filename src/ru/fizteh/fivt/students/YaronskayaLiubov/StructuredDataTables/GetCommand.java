package ru.fizteh.fivt.students.YaronskayaLiubov.StructuredDataTables;

import ru.fizteh.fivt.storage.structured.Storeable;

/**
 * Created by luba_yaronskaya on 19.10.14.
 */
public class GetCommand extends Command {
    GetCommand() {
        name = "get";
        numberOfArguments = 2;
    }

    boolean execute(MultiFileHashMap multiFileHashMap, String[] args) {
        if (args.length != numberOfArguments) {
            System.err.println(name + ": wrong number of arguements");
            return false;
        }
        if (multiFileHashMap.currTable == null) {
            System.err.println("no table");
            return false;
        }
        Storeable row = multiFileHashMap.currTable.get(args[1]);
        if (row == null) {
            System.out.println("not found");
        } else {
            for (int i = 0; i < multiFileHashMap.currTable.getColumnsCount(); ++i) {
                System.out.print(row.getColumnAt(i) + " ");
            }
            System.out.println();
        }
        return true;
    }
}
