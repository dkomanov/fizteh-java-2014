package ru.fizteh.fivt.students.ZatsepinMikhail.MultiFileHashMap.MultiFileHashMapPackage;

import ru.fizteh.fivt.students.ZatsepinMikhail.MultiFileHashMap.FileMapPackage.FileMap;
import ru.fizteh.fivt.students.ZatsepinMikhail.MultiFileHashMap.FileMapPackage.List;

public class CommandListDistribute extends CommandMultiFileHashMap {
    public CommandListDistribute() {
        name = "list";
        numberOfArguments = 1;
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
        List commandList = new List();
        return commandList.run(currentTable, args);
    }
}
