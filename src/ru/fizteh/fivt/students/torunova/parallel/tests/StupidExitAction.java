package ru.fizteh.fivt.students.torunova.parallel.tests;

import ru.fizteh.fivt.students.torunova.parallel.database.actions.Action;
import ru.fizteh.fivt.students.torunova.parallel.interpreter.ShellInterruptException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * Created by nastya on 19.12.14.
 */
public class StupidExitAction extends Action {
    PrintWriter writer;
    public StupidExitAction(OutputStream os) {
        writer = new PrintWriter(os, true);
    }
    @Override
    public boolean run(String args) throws IOException {
        if (args.equals("")) {
            writer.println(getName() + " 0");
        } else {
            writer.println(getName() + " 1");
        }
        throw new ShellInterruptException();
    }

    @Override
    public String getName() {
        return "exit";
    }
}
