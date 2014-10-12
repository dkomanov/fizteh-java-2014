package ru.fizteh.fivt.students.YaronskayaLiubov.FileMap;

/**
 * Created by luba_yaronskaya on 06.10.14.
 */
public class RemoveCommand extends Command {
    RemoveCommand() {
        name = "remove";
        numberOfArguements = 2;
    }
    boolean execute(String[] args) {
        if (args.length != numberOfArguements) {
            System.err.println(name + ": wrong number of arguements");
            return false;
        }
        String removedValue = FileMap.data.remove(args[1]);
        System.out.println((removedValue == null) ? "not found" : "removed");
        return true;
    }
}
