package ru.fizteh.fivt.students.vadim_mazaev.filemap.commands;

import ru.fizteh.fivt.students.vadim_mazaev.filemap.DbConnector;

public abstract class DbCommand implements Command {
    public DbCommand(final DbConnector link) {
        connector = link;
    }

    @Override
    public final String getName() {
        return this.getClass().getSimpleName();
    }

    public final DbConnector getConnector() {
        return connector;
    }

    @Override
    public abstract boolean checkArgs(int argLen);

    @Override
    public abstract void execute(String[] cmdWithArgs);

    private DbConnector connector;
}
