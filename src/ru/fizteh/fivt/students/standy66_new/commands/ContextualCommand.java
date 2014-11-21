package ru.fizteh.fivt.students.standy66_new.commands;

import java.io.PrintWriter;
import java.util.function.IntPredicate;

/**
 * @author andrew
 *         Created by andrew on 21.11.14.
 */
public class ContextualCommand extends Command {
    private final Object context;

    protected ContextualCommand(PrintWriter outputWriter, IntPredicate isValidNumberOfArguments, Object context) {
        super(outputWriter, isValidNumberOfArguments);
        this.context = context;
    }

    protected Object getContext() {
        return context;
    }
}
