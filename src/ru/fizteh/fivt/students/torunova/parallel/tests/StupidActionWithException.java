package ru.fizteh.fivt.students.torunova.parallel.tests;

import ru.fizteh.fivt.students.torunova.parallel.database.actions.Action;

import java.io.IOException;

/**
 * Created by nastya on 19.12.14.
 */
public class StupidActionWithException extends Action {
    @Override
    public boolean run(String args) throws IOException {
        throw new StupidException("exception");
    }

    @Override
    public String getName() {
        return "stupidExcept";
    }
}
