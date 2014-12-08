package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.CommandsTableProvider;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.StoreablePackage.Serializator;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.TableProviderExtended;

import java.io.PrintStream;

public class CommandGetDistribute extends CommandTableProviderExtended {
    public CommandGetDistribute() {
        name = "get";
        numberOfArguments = 2;
    }

    @Override
    public boolean run(TableProviderExtended myMap, String[] args, PrintStream output) {
        Table currentTable = myMap.getCurrentTable();
        if (myMap.getCurrentTable() == null) {
            output.println("no table");
            return true;
        }
        Storeable value = currentTable.get(args[1]);
        if (value != null) {
            output.println("found\n" + Serializator.serialize(currentTable, currentTable.get(args[1])));
        } else {
            output.println("not found");
        }
        return true;
    }
}
