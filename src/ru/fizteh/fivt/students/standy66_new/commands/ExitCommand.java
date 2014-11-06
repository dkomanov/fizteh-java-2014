package ru.fizteh.fivt.students.standy66_new.commands;

import ru.fizteh.fivt.students.standy66_new.exceptions.InterpreterInterruptionException;

/**
 * Created by astepanov on 20.10.14.
 */
public class ExitCommand extends Command {
    public ExitCommand() {
        super((x -> x == 1));
    }

    @Override
    public void run(String[] arguments) throws Exception {
        super.run(arguments);
        throw new InterpreterInterruptionException("got exit command");
    }
}
