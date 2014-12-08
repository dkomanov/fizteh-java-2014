package ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.MultiFileHashMap;

import ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.FileMap.FileMap;
import ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.FileMap.FmCommandRemove;

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
