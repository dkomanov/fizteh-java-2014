package ru.fizteh.fivt.students.dmitry_persiyanov.database.db_manager.commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_manager.DbManager;

import java.io.IOException;
import java.io.PrintStream;

public class CreateCommand extends DbManagerCommand {
    public CreateCommand(final String[] args, final DbManager relatedDb) {
        super("list", 0, args, relatedDb);
    }

    @Override
    protected void execute(final PrintStream out) throws IOException {
        String tableToCreate = args[0];
        if (!relatedDb.containsTable(tableToCreate)) {
            if (relatedDb.createTable(tableToCreate) == null) {
                throw new IllegalArgumentException(tableToCreate + " doesn't exist");
            }
            out.println("created");
        } else {
            out.println(tableToCreate + " exists");
        }
    }
}
