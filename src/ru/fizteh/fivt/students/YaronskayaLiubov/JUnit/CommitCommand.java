package ru.fizteh.fivt.students.YaronskayaLiubov.JUnit;

/**
 * Created by luba_yaronskaya on 10.11.14.
 */
public class CommitCommand extends Command {
    CommitCommand() {
        name = "commit";
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
        MultiFileHashMap.currTable.commit();
        return true;
    }
}
