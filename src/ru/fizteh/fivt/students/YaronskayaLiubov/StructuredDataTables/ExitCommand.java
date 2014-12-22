package ru.fizteh.fivt.students.YaronskayaLiubov.StructuredDataTables;

/**
 * Created by luba_yaronskaya on 18.10.14.
 */
public class ExitCommand extends Command {
    ExitCommand() {
        name = "exit";
        numberOfArguments = 1;
    }

    boolean execute(MultiFileHashMap multiFileHashMap, String[] args) {
        if (args.length != numberOfArguments) {
            System.err.println(name + ": wrong number of arguements");
            return false;
        }
        multiFileHashMap.save();
        System.exit(multiFileHashMap.errorOccurred ? 1 : 0);
        return true;
    }
}
