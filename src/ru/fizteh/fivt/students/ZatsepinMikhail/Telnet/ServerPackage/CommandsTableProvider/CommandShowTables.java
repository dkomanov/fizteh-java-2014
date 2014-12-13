package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.CommandsTableProvider;

import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.RealRemoteTableProvider;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.TableProviderExtended;

import java.io.PrintStream;
import java.util.List;

public class CommandShowTables extends CommandTableProviderExtended {
    public CommandShowTables() {
        name = "show";
        numberOfArguments = 2;
    }

    @Override
    public boolean run(TableProviderExtended myMap, String[] args, PrintStream output) {
        if (!args[1].equals("tables")) {
            output.println(name + ": wrong arguments");
            return false;
        }
        List<String> tables = myMap.showTables();
        output.println(tables.size());
        for (String oneTableName : tables) {
            output.println(oneTableName);
        }
        return true;
    }
}
