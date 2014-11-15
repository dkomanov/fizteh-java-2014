package ru.fizteh.fivt.students.dmitry_persiyanov.database.db_manager.commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.database.DatabaseCommand;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_manager.DbManager;

import java.io.IOException;
import java.io.PrintStream;

public abstract class DbManagerCommand extends DatabaseCommand {
    protected final DbManager relatedDb;

    public DbManagerCommand(final String name, int numOfArgs, final String[] args, final DbManager relatedDb) {
        super(name, numOfArgs, args);
        this.relatedDb = relatedDb;
    }

    @Override
    protected abstract void execute(final PrintStream out) throws IOException;
}
