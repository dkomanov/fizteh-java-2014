package ru.fizteh.fivt.students.YaronskayaLiubov.MultiFileHashMap;

/**
 * Created by luba_yaronskaya on 19.10.14.
 */
public class PutCommand extends Command {
    PutCommand() {
        name = "put";
        numberOfArguements = 3;
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
        String old = MultiFileHashMap.currTable.data.put(args[1], args[2]);
        if (old != null) {
            System.out.println("overwrite");
            System.out.println(old);
        } else {
            System.out.println("new");
        }
        return true;
    }
}
