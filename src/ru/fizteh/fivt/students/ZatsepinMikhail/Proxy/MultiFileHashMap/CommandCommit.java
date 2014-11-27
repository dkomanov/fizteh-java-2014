package ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.MultiFileHashMap;

import java.io.IOException;

public class CommandCommit extends ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.MultiFileHashMap.CommandMultiFileHashMap {
    public CommandCommit() {
        name = "commit";
        numberOfArguments = 1;
    }

    @Override
    public boolean run(ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.MultiFileHashMap.MFileHashMap myMap, String[] args) {
        ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap.FileMap currentTable = myMap.getCurrentTable();
        if (currentTable == null) {
            System.out.println("no table");
        } else {
            try {
                System.out.println(currentTable.commit());
            } catch (IOException e) {
                System.err.println("io exception while writing in file");
                return false;
            }
        }
        return true;
    }
}
