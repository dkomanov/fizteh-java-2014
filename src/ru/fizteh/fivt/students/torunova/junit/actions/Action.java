package ru.fizteh.fivt.students.torunova.junit.actions;

import ru.fizteh.fivt.students.torunova.junit.CurrentTable;
import ru.fizteh.fivt.students.torunova.junit.Database;
import ru.fizteh.fivt.students.torunova.junit.exceptions.IncorrectFileException;
import ru.fizteh.fivt.students.torunova.junit.exceptions.TableNotCreatedException;

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

    public abstract boolean run(String[] args, CurrentTable currentTable)
                                  throws IOException,
                                  IncorrectFileException,
                                  TableNotCreatedException;

    public abstract String getName();

    public String getDisplayName() {
        return getName();
    }
}

