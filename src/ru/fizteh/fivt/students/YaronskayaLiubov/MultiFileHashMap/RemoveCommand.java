package ru.fizteh.fivt.students.YaronskayaLiubov.MultiFileHashMap;

/**
 * Created by luba_yaronskaya on 19.10.14.
 */
public class RemoveCommand extends Command {
    RemoveCommand() {
        name = "remove";
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
        String removedValue = MultiFileHashMap.currTable.data.remove(args[1]);
        System.out.println((removedValue == null) ? "not found" : "removed");
        return true;
    }
}
