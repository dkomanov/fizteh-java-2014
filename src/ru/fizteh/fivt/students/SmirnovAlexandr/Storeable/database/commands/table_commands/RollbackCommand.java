package ru.fizteh.fivt.students.SmirnovAlexandr.Storeable.database.commands.table_commands;

import ru.fizteh.fivt.students.SmirnovAlexandr.Storeable.database.commands.DbCommand;
import ru.fizteh.fivt.students.SmirnovAlexandr.Storeable.database.db_table_provider.DbTableProvider;
import ru.fizteh.fivt.students.SmirnovAlexandr.Storeable.database.exceptions.TableIsNotChosenException;

import java.io.PrintStream;

public class RollbackCommand extends DbCommand {
    public RollbackCommand(final String[] args, final DbTableProvider tableProvider) {
        super("rollback", 0, args, tableProvider);
    }

    @Override
    protected void execChecked(final PrintStream out) throws TableIsNotChosenException {
        if (currentTable() == null) {
            throw new TableIsNotChosenException();
        } else {
            out.println(currentTable().rollback());
        }
    }
}
