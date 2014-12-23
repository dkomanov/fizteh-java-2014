package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.CommandsTableProvider;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.TableProviderExtended;

import java.io.IOException;
import java.io.PrintStream;

public class CommandCommit extends CommandTableProviderExtended {
    public CommandCommit() {
        name = "commit";
        numberOfArguments = 2;
    }

    @Override
    public boolean run(TableProviderExtended dataBase, String[] args, PrintStream output) {
        Table currentTable = dataBase.getTable(args[1]);
        if (currentTable == null) {
            output.println("there isn't table \"" + args[1] + "\" on server");
        } else {
            try {
                output.println(currentTable.commit());
            } catch (IOException e) {
                output.println("io exception while writing in file");
                return false;
            }
        }
        return true;
    }
}
