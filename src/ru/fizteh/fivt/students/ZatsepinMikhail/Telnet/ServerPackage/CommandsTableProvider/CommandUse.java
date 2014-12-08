package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.CommandsTableProvider;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.TableProviderExtended;

import java.io.PrintStream;

public class CommandUse extends CommandTableProviderExtended {
    public CommandUse() {
        name = "use";
        numberOfArguments = 2;
    }

    @Override
    public boolean run(TableProviderExtended myMap, String[] args, PrintStream output) {
        Table newCurrentTable = myMap.getTable(args[1]);
        if (newCurrentTable != null) {
            Table currentTable = myMap.getCurrentTable();
            if (currentTable == null) {
                myMap.setCurrentTable(newCurrentTable);
                output.println("using " + args[1]);
            } else if (currentTable.getNumberOfUncommittedChanges() > 0) {
                output.println(currentTable.getNumberOfUncommittedChanges() + " unsaved changes");
            } else {
                myMap.setCurrentTable(newCurrentTable);
                output.println("using " + args[1]);
            }
        } else {
            output.println(args[1] + " not exists");
        }
        return true;
    }
}
