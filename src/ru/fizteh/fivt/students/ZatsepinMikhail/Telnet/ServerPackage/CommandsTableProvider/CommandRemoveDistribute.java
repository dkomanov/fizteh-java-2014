package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.CommandsTableProvider;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap.FileMap;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.MFileHashMap;


import java.io.PrintStream;

public class CommandRemoveDistribute extends CommandTableProvider {
    public CommandRemoveDistribute() {
        name = "remove";
        numberOfArguments = 2;
    }

    @Override
    public boolean run(MFileHashMap myMap, String[] args, PrintStream output) {
        FileMap currentTable = myMap.getCurrentTable();
        if (myMap.getCurrentTable() == null) {
            output.println("no table");
            return true;
        }
        Storeable value = currentTable.remove(args[1]);
        if (value != null) {
            output.println("removed");
        } else {
            output.println("not found");
        }
        return true;
    }
}
