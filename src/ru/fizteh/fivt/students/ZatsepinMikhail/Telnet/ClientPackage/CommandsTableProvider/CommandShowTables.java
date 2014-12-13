package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ClientPackage.CommandsTableProvider;

import ru.fizteh.fivt.storage.structured.TableProvider;

import java.io.PrintStream;
import java.util.List;

public class CommandShowTables extends CommandTableProviderExtended {
    public CommandShowTables() {
        name = "show";
        numberOfArguments = 2;
    }

    @Override
    public boolean run(TableProvider dataBase, String[] args, PrintStream output) {
        if (!args[1].equals("tables")) {
            System.out.println(name + ": wrong arguments");
            return false;
        }
        System.out.println("table_name row_count");
        List<String> tablesDescription = dataBase.getTableNames();
        for (String oneTable : tablesDescription) {
            System.out.println(oneTable);
        }
        return true;
    }
}
