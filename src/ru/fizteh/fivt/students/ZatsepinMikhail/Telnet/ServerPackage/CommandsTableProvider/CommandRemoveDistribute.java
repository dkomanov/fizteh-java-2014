package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.CommandsTableProvider;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.Serializator;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.TableProviderExtended;


import java.io.PrintStream;

public class CommandRemoveDistribute extends CommandTableProviderExtended {
    public CommandRemoveDistribute() {
        name = "remove";
        numberOfArguments = 3;
    }

    @Override
    public boolean run(TableProviderExtended dataBase, String[] args, PrintStream output) {
        Table currentTable = dataBase.getTable(args[1]);
        if (currentTable == null) {
            output.println("there isn't table \"" + args[1] + "\" on server");
            return false;
        }
        Storeable value = currentTable.remove(args[2]);
        if (value != null) {
            output.println("removed");
            output.println(Serializator.serialize(currentTable, value));
        } else {
            output.println("not found");
        }
        return true;
    }
}
