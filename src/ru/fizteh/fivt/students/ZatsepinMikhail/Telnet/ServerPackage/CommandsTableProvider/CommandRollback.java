package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.CommandsTableProvider;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.TableProviderExtended;

import java.io.PrintStream;

public class CommandRollback extends CommandTableProviderExtended {
    public CommandRollback() {
        name = "rollback";
        numberOfArguments = 2;
    }

    @Override
    public boolean run(TableProviderExtended dataBase, String[] args, PrintStream output) {
        Table currentTable = dataBase.getTable(args[1]);
        if (currentTable == null) {
            output.println("there isn't table \"" + args[1] + "\" on server");
        } else {
            output.println(currentTable.rollback());
        }
        return true;
    }
}
