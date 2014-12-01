package ru.fizteh.fivt.students.YaronskayaLiubov.MultiFileHashMap;

import java.io.File;
import java.nio.file.Paths;

/**
 * Created by luba_yaronskaya on 18.10.14.
 */
public class DropCommand extends Command {
    DropCommand() {
        name = "drop";
        numberOfArguements = 2;
    }

    boolean execute(String[] args) {
        if (args.length != numberOfArguements) {
            System.err.println(name + ": wrong number of arguements");
            return false;
        }
        FileMap db = MultiFileHashMap.tables.get(args[1]);

        if (db == null) {
            System.out.println(args[1] + " not exist");
            return true;
        }
        if (MultiFileHashMap.currTable == db) {
            MultiFileHashMap.currTable = null;
        }

        MultiFileHashMap.tables.remove(args[1]);
        fileDelete(new File(Paths.get(MultiFileHashMap.dbDir).resolve(args[1]).toString()));
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
