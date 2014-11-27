package ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.MultiFileHashMap;

import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap.FmCommandGet;

public class CommandGetDistribute extends CommandMultiFileHashMap {
    public CommandGetDistribute() {
        name = "get";
        numberOfArguments = 2;
    }

    @Override
    public boolean run(ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.MultiFileHashMap.MFileHashMap myMap, String[] args) {
        ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap.FileMap currentTable = myMap.getCurrentTable();
        if (myMap.getCurrentTable() == null) {
            System.out.println("no table");
            return true;
        }
        ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap.FmCommandGet commandGet = new FmCommandGet();
        return commandGet.run(currentTable, args);
    }
}
