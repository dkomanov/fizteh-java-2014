package ru.fizteh.fivt.students.titov.JUnit.MultiFileHashMapPackage;

import ru.fizteh.fivt.students.titov.JUnit.FileMapPackage.FileMap;

public class CommandUse extends CommandMultiFileHashMap {
    public CommandUse() {
        name = "use";
        numberOfArguments = 2;
    }

    @Override
    public boolean run(MFileHashMap myMap, String[] args) {
        FileMap newCurrentTable = myMap.findTableByName(args[1]);
        if (newCurrentTable != null) {
            FileMap currentTable = myMap.getCurrentTable();
            if (currentTable == null) {
                myMap.setCurrentTable(newCurrentTable);
                System.err.println("using " + args[1]);
            } else if (currentTable.getNumberOfUncommitedChanges() > 0) {
                System.err.println(currentTable.getNumberOfUncommitedChanges() + " unsaved changes");
            } else {
                myMap.setCurrentTable(newCurrentTable);
                System.err.println("using " + args[1]);
            }
        } else {
            System.err.println(args[1] + " not exists");
        }
        return true;
    }
}
