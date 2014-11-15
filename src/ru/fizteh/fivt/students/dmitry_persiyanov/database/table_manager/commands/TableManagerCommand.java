package ru.fizteh.fivt.students.dmitry_persiyanov.database.table_manager.commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.database.DatabaseCommand;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.exceptions.TableIsNotChosenException;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.table_manager.TableManager;

import java.io.IOException;
import java.io.PrintStream;


public abstract class TableManagerCommand extends DatabaseCommand {
    protected final TableManager relatedTable;

    public TableManagerCommand(final String name, int numOfArgs, final String[] args, final TableManager relatedTable) {
        super(name, numOfArgs, args);
        this.relatedTable = relatedTable;
    }

    @Override
    protected abstract void execute(final PrintStream out) throws IOException, TableIsNotChosenException;
}
