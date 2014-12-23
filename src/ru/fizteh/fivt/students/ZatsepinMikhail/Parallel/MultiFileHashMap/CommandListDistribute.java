package ru.fizteh.fivt.students.ZatsepinMikhail.Parallel.MultiFileHashMap;

import ru.fizteh.fivt.students.ZatsepinMikhail.Parallel.FileMap.FileMap;
import ru.fizteh.fivt.students.ZatsepinMikhail.Parallel.FileMap.FmCommandList;

public class CommandListDistribute extends CommandMultiFileHashMap {
    public CommandListDistribute() {
        name = "list";
        numberOfArguments = 1;
    }

    @Override
    public boolean run(MFileHashMap myMap, String[] args) {
        FileMap currentTable = myMap.getCurrentTable();
        if (myMap.getCurrentTable() == null) {
            System.out.println("no table");
            return true;
        }
        FmCommandList commandList = new FmCommandList();
        return commandList.run(currentTable, args);
    }
}
