package ru.fizteh.fivt.students.titov.JUnit.MultiFileHashMapPackage;

import ru.fizteh.fivt.students.titov.JUnit.FileMapPackage.FileMap;

public class CommandCommit extends CommandMultiFileHashMap{
    public CommandCommit() {
        name = "commit";
        numberOfArguments = 1;
    }

    @Override
    public boolean run(MFileHashMap myMap, String[] args) {
        FileMap currentTable = myMap.getCurrentTable();
        if (!isTable(currentTable)) {
            System.err.println(currentTable.commit());
        }
        return true;
    }
}
