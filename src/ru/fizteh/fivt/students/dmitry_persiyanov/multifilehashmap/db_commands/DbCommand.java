package ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.DbManager;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.exceptions.IllegalNumberOfArgumentsException;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.exceptions.TableIsNotChosenException;

import java.io.IOException;

public abstract class DbCommand {
    protected static int NUM_OF_ARGS;
    protected String[] args = null;
    protected String msg = null;
    protected String name = null;

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
        if (args.length != NUM_OF_ARGS) {
            throw new IllegalNumberOfArgumentsException(getName());
        }
    }
}