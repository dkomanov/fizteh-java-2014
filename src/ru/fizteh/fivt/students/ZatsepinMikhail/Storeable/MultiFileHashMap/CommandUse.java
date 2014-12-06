package ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.MultiFileHashMap;

import ru.fizteh.fivt.students.ZatsepinMikhail.Storeable.FileMap.FileMap;

public class CommandUse extends CommandMultiFileHashMap {
    public CommandUse() {
        name = "use";
        numberOfArguments = 2;
    }

    @Override
    public boolean run(MFileHashMap myMap, String[] args) {
        FileMap newCurrentTable = (FileMap) myMap.getTable(args[1]);
        if (newCurrentTable != null) {
            FileMap currentTable = myMap.getCurrentTable();
            if (currentTable == null) {
                myMap.setCurrentTable(newCurrentTable);
                System.out.println("using " + args[1]);
            } else if (currentTable.getNumberOfUncommittedChanges() > 0) {
                System.out.println(currentTable.getNumberOfUncommittedChanges() + " unsaved changes");
            } else {
                myMap.setCurrentTable(newCurrentTable);
                System.out.println("using " + args[1]);
            }
        } else {
            System.out.println(args[1] + " not exists");
        }
        return true;
    }
}
