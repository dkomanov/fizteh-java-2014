package ru.fizteh.fivt.students.ZatsepinMikhail.MultiFileHashMap;

import ru.fizteh.fivt.students.ZatsepinMikhail.FileMap.FileMap;
import ru.fizteh.fivt.students.ZatsepinMikhail.FileMap.Remove;

public class CommandRemoveDistribute extends CommandMultiFileHashMap {
    public CommandRemoveDistribute() {
        name = "remove";
        numberOfArguments = 2;
    }

    @Override
    public boolean run(MFileHashMap myMap, String[] args) {
        if (numberOfArguments != args.length) {
            System.out.println(name + ": wrong number of arguments");
            return false;
        }

        FileMap currentTable = myMap.getCurrentTable();
        if (myMap.getCurrentTable() == null) {
            System.out.println("no table");
            return true;
        }
        Remove removeCommand = new Remove();
        return removeCommand.run(currentTable, args);
    }
}
