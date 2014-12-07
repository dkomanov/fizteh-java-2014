package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.CommandsTableProvider;

import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.MultiFileHashMap.MFileHashMap;

import java.io.PrintStream;

public class CommandShowTables extends CommandTableProvider {
    public CommandShowTables() {
        name = "show";
        numberOfArguments = 2;
    }

    @Override
    public boolean run(MFileHashMap myMap, String[] args, PrintStream output) {
        if (!args[1].equals("tables")) {
            output.println(name + ": wrong arguments");
            return false;
        }
        output.println("table_name row_count");
        myMap.showTables();
        return true;
    }
}
