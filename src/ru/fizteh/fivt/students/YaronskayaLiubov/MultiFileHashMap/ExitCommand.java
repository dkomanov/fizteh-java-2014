package ru.fizteh.fivt.students.YaronskayaLiubov.MultiFileHashMap;

/**
 * Created by luba_yaronskaya on 18.10.14.
 */
public class ExitCommand extends Command {
    ExitCommand() {
        name = "exit";
        numberOfArguements = 1;
    }

    boolean execute(String[] args) {
        if (args.length != numberOfArguements) {
            System.err.println(name + ": wrong number of arguements");
            return false;
        }
        MultiFileHashMap.save();
        System.exit(MultiFileHashMap.errorOccurred ? 1 : 0);
        return true;
    }
}
