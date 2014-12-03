package ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.exceptions.IllegalNumberOfArgumentsException;

public abstract class DbCommand {
    protected static int numOfArgs;
    protected String[] args;
    protected String msg;
    protected String name;

    public DbCommand(final String name, final String[] args) {
        this.args = args;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getMsg() {
        return msg;
    }

    public void checkArgs() throws IllegalNumberOfArgumentsException {
        if (args.length != numOfArgs) {
            throw new IllegalNumberOfArgumentsException(getName());
        }
    }
}
