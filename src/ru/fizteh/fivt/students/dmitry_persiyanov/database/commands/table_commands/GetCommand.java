package ru.fizteh.fivt.students.dmitry_persiyanov.database.commands.table_commands;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.commands.DbCommand;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.exceptions.TableIsNotChosenException;

import java.io.PrintStream;

public class GetCommand extends DbCommand {
    public GetCommand(final String[] args, final TableProvider tableProvider, final Table table) {
        super("get", 1, args, tableProvider, table);
    }

    @Override
    protected void execute(final PrintStream out) throws TableIsNotChosenException {
        if (currentTable == null) {
            throw new TableIsNotChosenException();
        } else {
            String key = args[0];
            Storeable value = currentTable.get(key);
            if (value == null) {
                out.println("not found");
            } else {
                out.println("found" + System.lineSeparator() + tableProvider.serialize(currentTable, value));
            }
        }
    }
}
