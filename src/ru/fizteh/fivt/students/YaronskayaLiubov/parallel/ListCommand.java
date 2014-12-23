package ru.fizteh.fivt.students.YaronskayaLiubov.parallel;

/**
 * Created by luba_yaronskaya on 19.10.14.
 */
public class ListCommand extends Command {
    ListCommand() {
        name = "list";
        numberOfArguments = 1;
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

        System.out.println(String.join(", ", multiFileHashMap.currTable.list()));
        return true;
    }
}
