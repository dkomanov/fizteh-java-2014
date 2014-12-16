package ru.fizteh.fivt.students.titov.JUnit.MultiFileHashMapPackage;

import ru.fizteh.fivt.students.titov.JUnit.FileMapPackage.FileMap;
import ru.fizteh.fivt.students.titov.JUnit.FileMapPackage.FmCommandGet;

public class CommandGetDistribute extends CommandMultiFileHashMap {
    public CommandGetDistribute() {
        name = "get";
        numberOfArguments = 2;
    }

    @Override
    public boolean run(MFileHashMap myMap, String[] args) {
        FileMap currentTable = myMap.getCurrentTable();
        if (isTable(currentTable)) {
            return true;
        }
        FmCommandGet commandGet = new FmCommandGet();
        return commandGet.run(currentTable, args);
    }
}
