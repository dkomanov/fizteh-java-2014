package ru.fizteh.fivt.students.YaronskayaLiubov.FileMap;

/**
 * Created by luba_yaronskaya on 12.10.14.
 */
public class ListCommand extends Command {
    ListCommand() {
        name = "list";
        numberOfArguements = 1;
    }
    boolean execute(String[] args) {
        if (args.length != numberOfArguements) {
            System.err.println(name + ": wrong number of arguements");
            return false;
        }
        boolean moreThenOne = false;
        for (String key : FileMap.data.keySet()) {
            System.out.print(moreThenOne ? ", " : "");
            System.out.print(key);
            moreThenOne = true;
        }
        System.out.println();
    return true;
    }
}
