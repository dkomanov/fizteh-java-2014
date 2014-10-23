package ru.fizteh.fivt.students.torunova.multifilehashmap.actions;

import ru.fizteh.fivt.students.torunova.multifilehashmap.Database;
import ru.fizteh.fivt.students.torunova.multifilehashmap.exceptions.IncorrectFileException;
import ru.fizteh.fivt.students.torunova.multifilehashmap.exceptions.TableNotCreatedException;

import java.io.IOException;

/**
 * Created by nastya on 21.10.14.
 */
public abstract class Action {
    void tooManyArguments() {
        System.err.println(getName() + ": too many arguments.");
    }
    void tooFewArguments() {
        System.err.println(getName() + ": too few arguments.");
    }

    public abstract boolean run(String[] args, Database db)
                                  throws IOException,
                                  IncorrectFileException,
                                  TableNotCreatedException;

    public abstract String getName();
}

