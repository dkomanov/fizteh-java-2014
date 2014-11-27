package ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.MultiFileHashMap;

import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap.FileMap;
import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap.FmCommandList;

public class CommandListDistribute extends ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.MultiFileHashMap.CommandMultiFileHashMap {
    public CommandListDistribute() {
        name = "list";
        numberOfArguments = 1;
    }

    @Override
    public boolean run(ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.MultiFileHashMap.MFileHashMap myMap, String[] args) {
        FileMap currentTable = myMap.getCurrentTable();
        if (myMap.getCurrentTable() == null) {
            System.out.println("no table");
            return true;
        }
        FmCommandList commandList = new ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap.FmCommandList();
        return commandList.run(currentTable, args);
    }
}
