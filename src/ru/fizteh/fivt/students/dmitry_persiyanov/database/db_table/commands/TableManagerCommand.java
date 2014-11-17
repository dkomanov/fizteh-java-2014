package ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table.commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.database.DbCommand;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.exceptions.TableIsNotChosenException;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table.DbTable;

import java.io.IOException;
import java.io.PrintStream;


public abstract class TableManagerCommand extends DbCommand {
    protected final DbTable relatedTable;

    public TableManagerCommand(final String name, int numOfArgs, final String[] args, final DbTable relatedTable) {
        super(name, numOfArgs, args);
        this.relatedTable = relatedTable;
    }

    @Override
    protected abstract void execute(final PrintStream out) throws IOException, TableIsNotChosenException;
}
