package ru.fizteh.fivt.students.titov.JUnit.MultiFileHashMapPackage;

import ru.fizteh.fivt.students.titov.JUnit.FileMapPackage.FileMap;
import ru.fizteh.fivt.students.titov.JUnit.FileMapPackage.FmCommandList;

public class CommandListDistribute extends CommandMultiFileHashMap {
    public CommandListDistribute() {
        name = "list";
        numberOfArguments = 1;
    }

    @Override
    public boolean run(MFileHashMap myMap, String[] args) {
        FileMap currentTable = myMap.getCurrentTable();
        if (isTable(currentTable)) {
            return true;
        }
        FmCommandList commandList = new FmCommandList();
        return commandList.run(currentTable, args);
    }
}
