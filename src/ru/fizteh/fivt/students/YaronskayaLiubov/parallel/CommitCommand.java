package ru.fizteh.fivt.students.YaronskayaLiubov.parallel;

import java.io.IOException;

/**
 * Created by luba_yaronskaya on 10.11.14.
 */
public class CommitCommand extends Command {
    CommitCommand() {
        name = "commit";
        numberOfArguments = 1;
    }

    boolean execute(MultiFileHashMap multiFileHashMap, String[] args) throws MultiFileMapRunTimeException {
        if (args.length != numberOfArguments) {
            System.err.println(name + ": wrong number of arguements");
            return false;
        }
        if (multiFileHashMap.currTable == null) {
            System.err.println("no table");
            return false;
        }
        multiFileHashMap.currTable.commit();
        return true;
    }
}
