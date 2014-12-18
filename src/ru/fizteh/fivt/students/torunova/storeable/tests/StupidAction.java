package ru.fizteh.fivt.students.torunova.storeable.tests;


import ru.fizteh.fivt.students.torunova.storeable.database.actions.Action;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Created by nastya on 18.12.14.
 */
public class StupidAction extends Action {
    PrintWriter writer;
    StupidAction(OutputStream os) {
        writer = new PrintWriter(os, true);
    }

    @Override
    public boolean run(String args) throws IOException {
        writer.println(getName() + " " + args);
        return true;
    }

    @Override
    public String getName() {
        return "stupid";
    }
}
