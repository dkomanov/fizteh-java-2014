package ru.fizteh.fivt.students.YaronskayaLiubov.proxy;

/**
 * Created by luba_yaronskaya on 10.11.14.
 */
public class RollbackCommand extends Command {
    RollbackCommand() {
        name = "rollback";
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
        multiFileHashMap.currTable.rollback();
        return true;
    }
}
