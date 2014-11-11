package ru.fizteh.fivt.students.YaronskayaLiubov.JUnit;

/**
 * Created by luba_yaronskaya on 19.10.14.
 */
public class ListCommand extends Command {
    ListCommand() {
        name = "list";
        numberOfArguments = 1;
    }

    boolean execute(String[] args) {
        if (args.length != numberOfArguments) {
            System.err.println(name + ": wrong number of arguements");
            return false;
        }
        if (MultiFileHashMap.currTable == null) {
            System.err.println("no table");
            return false;
        }
        boolean moreThenOne = false;
        for (String key : MultiFileHashMap.currTable.list()) {
            System.out.print(moreThenOne ? ", " : "");
            System.out.print(key);
            moreThenOne = true;
        }
        if (MultiFileHashMap.currTable.list().size() > 0) {
            System.out.println();
        }
        return true;
    }
}
