package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.CommandsTableProvider;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.TableProviderExtended;

import java.io.IOException;
import java.io.PrintStream;

public class CommandCommit extends CommandTableProviderExtended {
    public CommandCommit() {
        name = "commit";
        numberOfArguments = 1;
    }

    @Override
    public boolean run(TableProviderExtended myMap, String[] args, PrintStream output) {
        Table currentTable = myMap.getCurrentTable();
        if (currentTable == null) {
            output.println("no table");
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
