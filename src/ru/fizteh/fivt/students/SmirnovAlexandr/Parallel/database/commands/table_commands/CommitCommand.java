package ru.fizteh.fivt.students.SmirnovAlexandr.Parallel.database.commands.table_commands;

import ru.fizteh.fivt.students.SmirnovAlexandr.Parallel.database.commands.DbCommand;
import ru.fizteh.fivt.students.SmirnovAlexandr.Parallel.database.db_table_provider.DbTableProvider;
import ru.fizteh.fivt.students.SmirnovAlexandr.Parallel.database.exceptions.TableIsNotChosenException;

import java.io.IOException;
import java.io.PrintStream;

public class CommitCommand extends DbCommand {
    public CommitCommand(final String[] args, final DbTableProvider tableProvider) {
        super("commit", 0, args, tableProvider);
    }

    @Override
    protected void execChecked(final PrintStream out) throws TableIsNotChosenException, IOException {
        if (currentTable() == null) {
            throw new TableIsNotChosenException();
        } else {
            out.println(currentTable().commit());
        }
    }
}
