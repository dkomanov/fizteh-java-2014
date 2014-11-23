package ru.fizteh.fivt.students.dmitry_persiyanov.database.commands.table_commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.database.commands.DbCommand;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table_provider.DbTableProvider;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.exceptions.TableIsNotChosenException;

import java.io.PrintStream;

/**
 * Created by drack3800 on 13.11.2014.
 */
public class SizeCommand extends DbCommand {
    public SizeCommand(final String[] args, final DbTableProvider tableProvider) {
        super("size", 0, args, tableProvider);
    }

    @Override
    protected void execChecked(final PrintStream out) throws TableIsNotChosenException {
        if (currentTable() == null) {
            throw new TableIsNotChosenException();
        } else {
            int size = currentTable().size();
            out.println(size);
        }
    }
}
