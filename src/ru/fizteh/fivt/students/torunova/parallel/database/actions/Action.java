package ru.fizteh.fivt.students.torunova.parallel.database.actions;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by nastya on 21.10.14.
 */
public abstract class Action {
    void tooManyArguments(PrintWriter writer) {
        writer.println(getDisplayName() + ": too many arguments.");
    }

    void tooFewArguments(PrintWriter writer) {
        writer.println(getDisplayName() + ": too few arguments.");
    }

    boolean checkNumberOfArguments(int expected, int real, PrintWriter writer) {
        if (real > expected) {
            tooManyArguments(writer);
            return false;
        } else if (real < expected) {
            tooFewArguments(writer);
            return false;
        }
        return true;
    }

    public abstract boolean run(String args)
            throws IOException;
    public abstract String getName();

    public String getDisplayName() {
        return getName();
    }

    public String[] parseArguments(String notParsedArgs) {
        if (notParsedArgs == null) {
            return null;
        } else if (notParsedArgs.equals("")) {
            return new String[0];
        }
        return notParsedArgs.split("\\s+");
    }
}

