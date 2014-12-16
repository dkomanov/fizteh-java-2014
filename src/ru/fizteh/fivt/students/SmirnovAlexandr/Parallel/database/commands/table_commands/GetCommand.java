package ru.fizteh.fivt.students.SmirnovAlexandr.Parallel.database.commands.table_commands;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.SmirnovAlexandr.Parallel.database.commands.DbCommand;
import ru.fizteh.fivt.students.SmirnovAlexandr.Parallel.database.db_table_provider.DbTableProvider;
import ru.fizteh.fivt.students.SmirnovAlexandr.Parallel.database.exceptions.TableIsNotChosenException;

import java.io.PrintStream;

public class GetCommand extends DbCommand {
    public GetCommand(final String[] args, final DbTableProvider tableProvider) {
        super("get", 1, args, tableProvider);
    }

    @Override
    protected void execChecked(final PrintStream out) throws TableIsNotChosenException {
        if (currentTable() == null) {
            throw new TableIsNotChosenException();
        } else {
            String key = args[0];
            Storeable value = currentTable().get(key);
            if (value == null) {
                out.println("not found");
            } else {
                out.println("found" + System.lineSeparator() + tableProvider.serialize(currentTable(), value));
            }
        }
    }
}
