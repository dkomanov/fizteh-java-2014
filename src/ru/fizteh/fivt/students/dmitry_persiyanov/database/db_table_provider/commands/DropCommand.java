package ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table_provider.commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table_provider.DbTableProvider;

import java.io.IOException;
import java.io.PrintStream;

public class DropCommand extends DbTableProviderCommand {
    public DropCommand(final String[] args, final DbTableProvider relatedDb) {
        super("drop", 1, args, relatedDb);
    }

    @Override
    protected void execute(final PrintStream out) throws IOException {
        String tableToDrop = args[0];
        if (!relatedDb.containsTable(tableToDrop)) {
            out.println(tableToDrop + " not exists");
        } else {
            relatedDb.removeTable(tableToDrop);
            out.println("dropped");
        }
    }
}
