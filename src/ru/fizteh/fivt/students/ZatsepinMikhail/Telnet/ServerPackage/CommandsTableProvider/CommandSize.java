package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.CommandsTableProvider;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.TableProviderExtended;

import java.io.PrintStream;

public class CommandSize extends CommandTableProviderExtended {
    public CommandSize() {
        name = "size";
        numberOfArguments = 1;
    }

    @Override
    public boolean run(TableProviderExtended myMap, String[] args, PrintStream output) {
        Table currentTable = myMap.getCurrentTable();
        if (currentTable == null) {
            output.println("no table");
        } else {
            output.println(currentTable.size());
        }
        return true;
    }
}
