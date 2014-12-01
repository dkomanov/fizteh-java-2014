package ru.fizteh.fivt.students.YaronskayaLiubov.MultiFileHashMap;

import java.util.Map;

/**
 * Created by luba_yaronskaya on 18.10.14.
 */
public class ShowCommand extends Command {
    ShowCommand() {
        name = "show";
        numberOfArguements = 2;
    }

    boolean execute(String[] args) {
        if (args.length != numberOfArguements) {
            System.err.println(name + ": wrong number of arguements");
            return false;
        } else {
            if (!args[1].equals("tables")) {
                System.err.println("incorrect arguement");
                return false;
            }
            for (Map.Entry<String, FileMap> entry : MultiFileHashMap.tables.entrySet()) {
                System.out.println(entry.getKey() + " " + entry.getValue().data.size());
            }
            return true;
        }
    }
}
