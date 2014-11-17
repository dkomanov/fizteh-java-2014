package ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table.commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.database.exceptions.TableIsNotChosenException;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table.DbTable;

import java.io.PrintStream;
import java.util.List;

public class ListCommand extends TableManagerCommand {
    public ListCommand(final String[] args, final DbTable relatedTable) {
        super("list", 0, args, relatedTable);
    }

    @Override
    protected void execute(final PrintStream out) throws TableIsNotChosenException {
        if (relatedTable == null) {
            throw new TableIsNotChosenException();
        } else {
            List<String> allKeys = relatedTable.list();
            if (allKeys.size() != 0) {
                out.println(String.join(", ", allKeys));
            } else {
                out.print("");
            }
        }
    }
}
