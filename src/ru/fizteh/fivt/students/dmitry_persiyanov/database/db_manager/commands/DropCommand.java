package ru.fizteh.fivt.students.dmitry_persiyanov.database.db_manager.commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_manager.DbManager;

import java.io.IOException;
import java.io.PrintStream;

public class DropCommand extends DbManagerCommand {
    public DropCommand(final String[] args, final DbManager relatedDb) {
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
