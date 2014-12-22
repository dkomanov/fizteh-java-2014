package ru.fizteh.fivt.students.torunova.storeable.tests;

import ru.fizteh.fivt.students.torunova.storeable.database.actions.Action;

import java.io.IOException;

/**
 * Created by nastya on 18.12.14.
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
