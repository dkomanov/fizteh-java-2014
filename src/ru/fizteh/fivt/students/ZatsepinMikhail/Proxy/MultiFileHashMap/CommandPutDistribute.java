package ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.MultiFileHashMap;

import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap.FmCommandPut;

public class CommandPutDistribute extends ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.MultiFileHashMap.CommandMultiFileHashMap {
    public CommandPutDistribute() {
        name = "put";
        numberOfArguments = -1;
    }

    @Override
    public boolean run(ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.MultiFileHashMap.MFileHashMap myMap, String[] args) {
        ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap.FileMap currentTable = myMap.getCurrentTable();
        if (myMap.getCurrentTable() == null) {
            System.out.println("no table");
            return true;
        }
        FmCommandPut commandPut = new ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap.FmCommandPut();
        return commandPut.run(currentTable, args);
    }
}
