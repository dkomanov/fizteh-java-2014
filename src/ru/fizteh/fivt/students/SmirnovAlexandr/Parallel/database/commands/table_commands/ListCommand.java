package ru.fizteh.fivt.students.SmirnovAlexandr.Parallel.database.commands.table_commands;

import ru.fizteh.fivt.students.SmirnovAlexandr.Parallel.database.commands.DbCommand;
import ru.fizteh.fivt.students.SmirnovAlexandr.Parallel.database.db_table_provider.DbTableProvider;
import ru.fizteh.fivt.students.SmirnovAlexandr.Parallel.database.exceptions.TableIsNotChosenException;

import java.io.PrintStream;
import java.util.List;

public class ListCommand extends DbCommand {
    public ListCommand(final String[] args, final DbTableProvider tableProvider) {
        super("list", 0, args, tableProvider);
    }

    @Override
    protected void execChecked(final PrintStream out) throws TableIsNotChosenException {
        if (currentTable() == null) {
            throw new TableIsNotChosenException();
        } else {
            List<String> allKeys = currentTable().list();
            if (allKeys.size() != 0) {
                out.println(String.join(", ", allKeys));
            }
        }
    }
}
