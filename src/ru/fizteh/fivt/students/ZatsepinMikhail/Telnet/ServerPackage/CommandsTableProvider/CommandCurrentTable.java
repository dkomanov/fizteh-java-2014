package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.CommandsTableProvider;

import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.TableProviderExtended;

import java.io.PrintStream;

public class CommandCurrentTable extends CommandTableProviderExtended {
    public CommandCurrentTable() {
        name = "current";
        numberOfArguments = 1;
    }

    @Override
    public boolean run(TableProviderExtended myMap, String[] args, PrintStream output) {
        if (myMap.getCurrentTable() == null) {
            output.println();
        } else {
            output.println(myMap.getCurrentTable().getName());
        }
        return true;
    }
}
