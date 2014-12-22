package ru.fizteh.fivt.students.dmitry_persiyanov.database.commands.tableprovider_commands;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.commands.DbCommand;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table_provider.DbTableProvider;

import java.io.IOException;
import java.io.PrintStream;

public class UseCommand extends DbCommand {
    public UseCommand(final String[] args, final DbTableProvider tableProvider) {
        super("use", 1, args, tableProvider);
    }

    @Override
    protected void execChecked(final PrintStream out) throws IOException {
        String tableToUse = args[0];
        Table newTable = tableProvider.getTable(tableToUse);
        if (newTable == null) {
            out.println(tableToUse + " not exists");
        } else {
            int unsavedChanges = tableProvider.useTable(tableToUse);
            if (unsavedChanges == 0) {
                out.println("using " + tableToUse);
            } else {
                out.println(unsavedChanges + " unsaved changes");
            }
        }
    }
}
