package ru.fizteh.fivt.students.dmitry_persiyanov.database.commands.table_commands;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.commands.DbCommand;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table_provider.DbTableProvider;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.exceptions.TableIsNotChosenException;

import java.io.PrintStream;

public class RemoveCommand extends DbCommand {
    public RemoveCommand(final String[] args, final DbTableProvider tableProvider) {
        super("remove", 1, args, tableProvider);
    }

    @Override
    protected void execChecked(final PrintStream out) throws TableIsNotChosenException {
        if (currentTable() == null) {
            throw new TableIsNotChosenException();
        } else {
            String key = args[0];
            Storeable oldValue = currentTable().remove(key);
            if (oldValue == null) {
                out.println("not found");
            } else {
                out.println("removed");
            }
        }
    }
}
