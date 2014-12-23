package ru.fizteh.fivt.students.YaronskayaLiubov.proxy;

import ru.fizteh.fivt.storage.structured.Storeable;

import java.text.ParseException;

/**
 * Created by luba_yaronskaya on 19.10.14.
 */
public class PutCommand extends Command {
    PutCommand() {
        name = "put";
        numberOfArguments = 3;
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

        try {
            Storeable row = multiFileHashMap.provider.deserialize(multiFileHashMap.currTable, args[2]);
            Storeable old = multiFileHashMap.currTable.put(args[1], row);
            if (old != null) {
                System.out.println("overwrite");
            } else {
                System.out.println("new");
            }
        } catch (ParseException e) {
            System.err.println(e.getMessage());
        }
        return true;
    }
}
