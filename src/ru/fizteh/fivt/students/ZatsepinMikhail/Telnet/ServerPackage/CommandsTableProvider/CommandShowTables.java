package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.CommandsTableProvider;


import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.MFileHashMap;

import java.io.PrintStream;
import java.util.List;

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
        List<String> tables = myMap.showTables();
        for (String oneTableName : tables) {
            output.println(oneTableName);
        }
        return true;
    }
}
