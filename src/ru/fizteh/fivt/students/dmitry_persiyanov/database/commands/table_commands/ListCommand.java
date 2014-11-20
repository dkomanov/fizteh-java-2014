package ru.fizteh.fivt.students.dmitry_persiyanov.database.commands.table_commands;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.commands.DbCommand;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.exceptions.TableIsNotChosenException;

import java.io.PrintStream;
import java.util.List;

public class ListCommand extends DbCommand {
    public ListCommand(final String[] args, final TableProvider tableProvider, final Table table) {
        super("list", 0, args, tableProvider, table);
    }

    @Override
    protected void execute(final PrintStream out) throws TableIsNotChosenException {
        if (currentTable == null) {
            throw new TableIsNotChosenException();
        } else {
            List<String> allKeys = currentTable.list();
            if (allKeys.size() != 0) {
                out.println(String.join(", ", allKeys));
            } else {
                out.print("");
            }
        }
    }
}
