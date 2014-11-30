package ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.MultiFileHashMap;

import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap.FileMap;

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
            try {
                System.out.println(currentTable.rollback());
            } catch (IllegalStateException e) {
                System.out.println(e.getMessage());
                return false;
            }
        }
        return true;
    }
}
