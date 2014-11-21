package ru.fizteh.fivt.students.standy66_new.commands;

import ru.fizteh.fivt.students.standy66_new.exceptions.InterpreterInterruptionException;

import java.io.PrintWriter;

/**
 * Created by astepanov on 20.10.14.
 */
public class ExitCommand extends Command {
    public ExitCommand(PrintWriter printWriter) {
        super(printWriter, (x -> x == 1));
    }

    @Override
    public void execute(String... arguments) throws Exception {
        super.execute(arguments);
        throw new InterpreterInterruptionException();
    }
}
