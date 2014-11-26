package ru.fizteh.fivt.students.torunova.storeable.actions;

import ru.fizteh.fivt.students.torunova.storeable.DatabaseWrapper;
import ru.fizteh.fivt.students.torunova.storeable.exceptions.IncorrectFileException;
import ru.fizteh.fivt.students.torunova.storeable.exceptions.TableNotCreatedException;

import java.io.IOException;

/**
 * Created by nastya on 21.10.14.
 */
public abstract class Action {
    void tooManyArguments() {
        System.err.println(getDisplayName() + ": too many arguments.");
    }

    void tooFewArguments() {
        System.err.println(getDisplayName() + ": too few arguments.");
    }

    boolean checkNumberOfArguments(int expected, int real) {
        if (real > expected) {
            tooManyArguments();
            return false;
        } else if (real < expected) {
            tooFewArguments();
            return false;
        }
        return true;
    }

    public abstract boolean run(String[] args, DatabaseWrapper db)
                                  throws IOException,
                                  IncorrectFileException,
                                  TableNotCreatedException;

    public abstract String getName();

    public String getDisplayName() {
        return getName();
    }
}

