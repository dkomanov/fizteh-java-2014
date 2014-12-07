package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.CommandsTableProvider;

import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap.FileMap;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.MFileHashMap;

import java.io.PrintStream;

public class CommandSize extends CommandTableProvider {
    public CommandSize() {
        name = "size";
        numberOfArguments = 1;
    }

    @Override
    public boolean run(MFileHashMap myMap, String[] args, PrintStream output) {
        FileMap currentTable = myMap.getCurrentTable();
        if (currentTable == null) {
            output.println("no table");
        } else {
            output.println(currentTable.size());
        }
        return true;
    }
}
