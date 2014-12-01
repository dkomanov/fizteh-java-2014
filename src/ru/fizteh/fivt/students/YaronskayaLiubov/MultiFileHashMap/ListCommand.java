package ru.fizteh.fivt.students.YaronskayaLiubov.MultiFileHashMap;

/**
 * Created by luba_yaronskaya on 19.10.14.
 */
public class ListCommand extends Command {
    ListCommand() {
        name = "list";
        numberOfArguements = 1;
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
        boolean moreThenOne = false;
        for (String key : MultiFileHashMap.currTable.data.keySet()) {
            System.out.print(moreThenOne ? ", " : "");
            System.out.print(key);
            moreThenOne = true;
        }
        System.out.println();
        return true;
    }
}
