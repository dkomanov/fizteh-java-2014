package ru.fizteh.fivt.students.YaronskayaLiubov.JUnit;

import ru.fizteh.fivt.storage.strings.Table;

/**
 * Created by luba_yaronskaya on 18.10.14.
 */
public class ShowCommand extends Command {
    ShowCommand() {
        name = "show";
        numberOfArguments = 2;
    }

    boolean execute(String[] args) {
        if (args.length != numberOfArguments) {
            System.err.println(name + ": wrong number of arguements");
            return false;
        } else {
            if (!args[1].equals("tables")) {
                System.err.println("incorrect arguement");
                return false;
            }
            for (Table table : MultiFileHashMap.provider.getTables().values()) {
                System.out.println(table.getName() + " " + table.size());
            }
            return true;
        }
    }
}
