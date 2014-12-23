package ru.fizteh.fivt.students.YaronskayaLiubov.proxy;

import ru.fizteh.fivt.storage.structured.Table;

import java.io.File;

/**
 * Created by luba_yaronskaya on 18.10.14.
 */
public class DropCommand extends Command {
    DropCommand() {
        name = "drop";
        numberOfArguments = 2;
    }

    boolean execute(MultiFileHashMap multiFileHashMap, String[] args) {
        if (args.length != numberOfArguments) {
            System.err.println(name + ": wrong number of arguements");
            return false;
        }
        Table db = multiFileHashMap.provider.getTable(args[1]);

        if (db == null) {
            System.out.println(args[1] + " not exist");
            return true;
        }
        if (multiFileHashMap.currTable == db) {
            multiFileHashMap.currTable = null;
        }

        multiFileHashMap.provider.removeTable(args[1]);
        System.out.println("dropped");
        return true;
    }

    private static void fileDelete(File myDir) {
        if (myDir.isDirectory()) {
            File[] content = myDir.listFiles();
            for (int i = 0; i < content.length; ++i) {
                fileDelete(content[i]);
            }
        }
        myDir.delete();
    }
}
