package ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table_provider.commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table_provider.DbTableProvider;

import java.io.IOException;
import java.io.PrintStream;

public class CreateCommand extends DbTableProviderCommand {
    public CreateCommand(final String[] args, final DbTableProvider relatedDb) {
        super("create", 1, args, relatedDb);
    }

    @Override
    protected void execute(final PrintStream out) throws IOException {
        String tableToCreate = args[0];
        if (!relatedDb.containsTable(tableToCreate)) {
            if (relatedDb.createTable(tableToCreate) == null) {
                out.println(tableToCreate + " doesn't exist");
            } else {
                out.println("created");
            }
        } else {
            out.println(tableToCreate + " exists");
        }
    }
}
