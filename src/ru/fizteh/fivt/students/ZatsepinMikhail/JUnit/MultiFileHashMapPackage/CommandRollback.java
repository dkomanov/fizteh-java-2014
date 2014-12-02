package ru.fizteh.fivt.students.ZatsepinMikhail.JUnit.MultiFileHashMapPackage;

import ru.fizteh.fivt.students.ZatsepinMikhail.JUnit.FileMapPackage.FileMap;

public class CommandRollback extends CommandMultiFileHashMap {
    public CommandRollback() {
        name = "rollback";
        numberOfArguments = 1;
    }

    @Override
    public boolean run(MFileHashMap myMap, String[] args) {
        FileMap currentTable = myMap.getCurrentTable();
        if (currentTable == null) {
            System.out.println("no table");
        } else {
            System.out.println(currentTable.rollback());
        }
        return true;
    }
}
