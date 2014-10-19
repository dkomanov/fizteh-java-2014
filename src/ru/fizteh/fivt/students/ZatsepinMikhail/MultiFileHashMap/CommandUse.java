package ru.fizteh.fivt.students.ZatsepinMikhail.MultiFileHashMap;

import ru.fizteh.fivt.students.ZatsepinMikhail.FileMap.FileMap;

/**
 * Created by mikhail on 17.10.14.
 */
public class CommandUse extends CommandMultiFileHashMap {
    public CommandUse() {
        name = "use";
        numberOfArguments = 2;
    }

    @Override
    public boolean run(MFileHashMap myMap, String[] args) {
        if (numberOfArguments != args.length) {
            System.out.println(name + ": wrong number of arguments");
            return false;
        }
        FileMap newCurrentTable = myMap.findTableByName(args[1]);
        if (newCurrentTable != null) {
            myMap.setCurrentTable(newCurrentTable);
            System.out.println("using " + args[1]);
        } else {
            System.out.println(args[1] + " not exists");
        }
        return true;
    }
}
