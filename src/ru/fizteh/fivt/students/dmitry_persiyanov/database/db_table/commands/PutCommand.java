package ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table.commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.database.exceptions.TableIsNotChosenException;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table.DbTable;

import java.io.PrintStream;

public class PutCommand extends TableManagerCommand {
    public PutCommand(final String[] args, final DbTable relatedTable) {
        super("put", 2, args, relatedTable);
    }

    @Override
    protected void execute(final PrintStream out) throws TableIsNotChosenException {
        if (relatedTable == null) {
            throw new TableIsNotChosenException();
        } else {
            String key = args[0];
            String value = args[1];
            String oldValue = relatedTable.put(key, value);
            if (oldValue == null) {
                out.println("new");
            } else {
                out.println("overwrite" + System.lineSeparator() + oldValue);
            }
        }
    }
}
