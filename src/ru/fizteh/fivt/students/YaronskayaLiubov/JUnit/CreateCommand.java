package ru.fizteh.fivt.students.YaronskayaLiubov.JUnit;

/**
 * Created by luba_yaronskaya on 18.10.14.
 */
public class CreateCommand extends Command {
    CreateCommand() {
        name = "create";
        numberOfArguments = 2;
    }

    boolean execute(String[] args) {
        if (args.length != numberOfArguments) {
            System.err.println(name + ": wrong number of arguements");
            return false;
        }
        MultiFileHashMap.provider.createTable(args[1]);
        return true;
    }
}
