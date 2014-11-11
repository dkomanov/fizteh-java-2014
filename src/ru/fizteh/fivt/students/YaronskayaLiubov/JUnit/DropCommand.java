package ru.fizteh.fivt.students.YaronskayaLiubov.JUnit;

import ru.fizteh.fivt.storage.strings.Table;

import java.io.File;

/**
 * Created by luba_yaronskaya on 18.10.14.
 */
public class DropCommand extends Command {
    DropCommand() {
        name = "drop";
        numberOfArguments = 2;
    }

    boolean execute(String[] args) {
        if (args.length != numberOfArguments) {
            System.err.println(name + ": wrong number of arguements");
            return false;
        }
        Table db = MultiFileHashMap.provider.getTable(args[1]);

        if (db == null) {
            System.out.println(args[1] + " not exist");
            return true;
        }
        if (MultiFileHashMap.currTable == db) {
            MultiFileHashMap.currTable = null;
        }

        MultiFileHashMap.provider.removeTable(args[1]);
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
