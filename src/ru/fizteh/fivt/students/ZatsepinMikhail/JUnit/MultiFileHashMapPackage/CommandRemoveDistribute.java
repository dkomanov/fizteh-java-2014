package ru.fizteh.fivt.students.ZatsepinMikhail.JUnit.MultiFileHashMapPackage;

import ru.fizteh.fivt.students.ZatsepinMikhail.JUnit.FileMapPackage.FileMap;
import ru.fizteh.fivt.students.ZatsepinMikhail.JUnit.FileMapPackage.FmCommandRemove;

public class CommandRemoveDistribute extends CommandMultiFileHashMap {
    public CommandRemoveDistribute() {
        name = "remove";
        numberOfArguments = 2;
    }

    @Override
    public boolean run(MFileHashMap myMap, String[] args) {
        FileMap currentTable = myMap.getCurrentTable();
        if (myMap.getCurrentTable() == null) {
            System.out.println("no table");
            return true;
        }
        FmCommandRemove removeCommand = new FmCommandRemove();
        return removeCommand.run(currentTable, args);
    }
}
