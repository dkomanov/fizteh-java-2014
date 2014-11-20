package ru.fizteh.fivt.students.dmitry_persiyanov.database.commands.table_commands;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.commands.DbCommand;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table_provider.DbTableProvider;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.exceptions.TableIsNotChosenException;

import java.io.PrintStream;

/**
 * Created by drack3800 on 13.11.2014.
 */
public class RollbackCommand extends DbCommand {
    public RollbackCommand(final String[] args, final DbTableProvider tableProvider, final Table table) {
        super("rollback", 0, args, tableProvider, table);
    }

    @Override
    protected void execute(final PrintStream out) throws TableIsNotChosenException {
        if (currentTable == null) {
            throw new TableIsNotChosenException();
        } else {
            out.println(currentTable.rollback());
        }
    }
}
