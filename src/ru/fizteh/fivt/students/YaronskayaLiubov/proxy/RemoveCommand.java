package ru.fizteh.fivt.students.YaronskayaLiubov.proxy;

import ru.fizteh.fivt.storage.structured.Storeable;

/**
 * Created by luba_yaronskaya on 19.10.14.
 */
public class RemoveCommand extends Command {
    RemoveCommand() {
        name = "remove";
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
        Storeable removedRow = multiFileHashMap.currTable.remove(args[1]);
        System.out.println((removedRow == null) ? "not found" : "removed");
        return true;
    }
}
