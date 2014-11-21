package ru.fizteh.fivt.students.standy66_new.commands;

import ru.fizteh.fivt.students.standy66_new.exceptions.WrongNumberOfArgumentsException;

import java.io.PrintWriter;
import java.util.function.IntPredicate;

/**
 * Created by astepanov on 20.10.14.
 */
public abstract class Command {
    private final PrintWriter outputWriter;
    private final IntPredicate isValidNumberOfArguments;

    protected Command(PrintWriter outputWriter, IntPredicate isValidNumberOfArguments) {
        this.outputWriter = outputWriter;
        this.isValidNumberOfArguments = isValidNumberOfArguments;
    }

    public void execute(String... arguments) throws Exception {
        if (!isValidNumberOfArguments.test(arguments.length)) {
            throw new WrongNumberOfArgumentsException("invalid number of arguments");
        }
    }

    protected PrintWriter getOutputWriter() {
        return outputWriter;
    }
}
