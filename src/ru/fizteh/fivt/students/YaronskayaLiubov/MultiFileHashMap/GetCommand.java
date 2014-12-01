package ru.fizteh.fivt.students.YaronskayaLiubov.MultiFileHashMap;

/**
 * Created by luba_yaronskaya on 19.10.14.
 */
public class GetCommand extends Command {
    GetCommand() {
        name = "get";
        numberOfArguements = 2;
    }

    boolean execute(String[] args) {
        if (args.length != numberOfArguements) {
            System.err.println(name + ": wrong number of arguements");
            return false;
        }
        if (MultiFileHashMap.currTable == null) {
            System.err.println("no table");
            return false;
        }
        String value = MultiFileHashMap.currTable.data.get(args[1]);
        System.out.println((value == null) ? "not found" : value);
        return true;
    }
}
