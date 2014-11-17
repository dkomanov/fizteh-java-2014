package ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table_provider.commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.database.DbCommand;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table_provider.DbTableProvider;

import java.io.IOException;
import java.io.PrintStream;

public abstract class DbTableProviderCommand extends DbCommand {
    protected final DbTableProvider relatedDb;

    public DbTableProviderCommand(final String name, int numOfArgs, final String[] args, final DbTableProvider relatedDb) {
        super(name, numOfArgs, args);
        this.relatedDb = relatedDb;
    }

    @Override
    protected abstract void execute(final PrintStream out) throws IOException;
}
