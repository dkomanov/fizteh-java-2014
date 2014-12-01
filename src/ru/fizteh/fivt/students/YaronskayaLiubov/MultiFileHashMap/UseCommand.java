package ru.fizteh.fivt.students.YaronskayaLiubov.MultiFileHashMap;

/**
 * Created by luba_yaronskaya on 18.10.14.
 */
public class UseCommand extends Command {
    UseCommand() {
        name = "use";
        numberOfArguements = 2;
    }

    boolean execute(String[] args) {
        if (args.length != numberOfArguements) {
            System.err.println(name + ": wrong number of arguements");
            return false;
        }

        String tableName = args[1];
        FileMap table = MultiFileHashMap.tables.get(tableName);
        if (table == null) {
            System.out.println(tableName + " not exists");
        } else {
            MultiFileHashMap.currTable = table;
            System.out.println("using " + tableName);
        }
        return true;
    }
}
