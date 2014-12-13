package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.CommandsTableProvider;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.StoreablePackage.Serializator;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.TableProviderExtended;

import java.io.PrintStream;

public class CommandGetDistribute extends CommandTableProviderExtended {
    public CommandGetDistribute() {
        name = "get";
        numberOfArguments = 3;
    }

    @Override
    public boolean run(TableProviderExtended dataBase, String[] args, PrintStream output) {
        Table currentTable = dataBase.getTable(args[1]);
        if (currentTable == null) {
            output.println("there isn't table \"" + args[1] + "\" on server");
            return false;
        }
        Storeable value = currentTable.get(args[2]);
        if (value != null) {
            output.println("found\n" + Serializator.serialize(currentTable, value));
        } else {
            output.println("not found");
        }
        return true;
    }
}
