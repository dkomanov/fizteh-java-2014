package ru.fizteh.fivt.students.dmitry_persiyanov.database.commands.tableprovider_commands;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.commands.DbCommand;

import java.io.IOException;
import java.io.PrintStream;

public class UseCommand extends DbCommand {
    public UseCommand(final String[] args, final TableProvider tableProvider) {
        super("use", 1, args, tableProvider);
    }

    @Override
    protected void execute(final PrintStream out) throws IOException {
        String tableToUse = args[0];
        Table newTable = tableProvider.getTable(tableToUse);
        if (newTable == null) {
            out.println(tableToUse + " not exists");
        } else {
            int unsavedChanges = newTable.getNumberOfUncommittedChanges();
            if (unsavedChanges == 0) {
                currentTable = newTable;
                out.println("using " + tableToUse);
            } else {
                out.println(unsavedChanges + " unsaved changes");
            }
        }
    }
}
