package ru.fizteh.fivt.students.dmitry_persiyanov.database.commands.tableprovider_commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.database.commands.DbCommand;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table_provider.DbTableProvider;

import java.io.IOException;
import java.io.PrintStream;

public class DropCommand extends DbCommand {
    public DropCommand(final String[] args, final DbTableProvider tableProvider) {
        super("drop", 1, args, tableProvider);
    }

    @Override
    protected void execChecked(final PrintStream out) throws IOException {
        String tableToDrop = args[0];
        if (tableProvider.getTable(tableToDrop) == null) {
            out.println(tableToDrop + " not exists");
        } else {
            tableProvider.removeTable(tableToDrop);
            out.println("dropped");
        }
    }
}
