package ru.fizteh.fivt.students.dmitry_persiyanov.database.db_manager.commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_manager.DbManager;

import java.io.IOException;
import java.io.PrintStream;

public class UseCommand extends DbManagerCommand {
    public UseCommand(final String[] args, final DbManager relatedDb) {
        super("use", 1, args, relatedDb);
    }

    @Override
    protected void execute(final PrintStream out) throws IOException {
        String tableToUse = args[0];
        if (!relatedDb.containsTable(tableToUse)) {
            out.println(tableToUse + " not exists");
        } else {
            int unsavedChanges = relatedDb.useTable(tableToUse);
            if (unsavedChanges == 0) {
                out.println("using " + tableToUse);
            } else {
                out.println(unsavedChanges + " unsaved changes");
            }
        }
    }
}
