package ru.fizteh.fivt.students.YaronskayaLiubov.MultiFileHashMap;


/**
 * Created by luba_yaronskaya on 18.10.14.
 */
public class CreateCommand extends Command {
    CreateCommand() {
        name = "create";
        numberOfArguements = 2;
    }

    boolean execute(String[] args) {
        if (args.length != numberOfArguements) {
            System.err.println(name + ": wrong number of arguements");
            return false;
        }
        if (MultiFileHashMap.tables.get(args[1]) == null) {
            MultiFileHashMap.tables.put(args[1], new FileMap(MultiFileHashMap.dbDir, args[1]));
            System.out.println("created");
        } else {
            System.out.println(args[1] + " exists");
        }
        return true;
    }
}
