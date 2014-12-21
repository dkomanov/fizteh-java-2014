package ru.fizteh.fivt.students.standy66_new.server.commands;

import ru.fizteh.fivt.students.standy66_new.commands.ContextualCommand;

import java.io.PrintWriter;
import java.util.function.IntPredicate;

/**
 * @author andrew
 *         Created by andrew on 30.11.14.
 */
public class ServerContextualCommand extends ContextualCommand {
    protected ServerContextualCommand(PrintWriter outputWriter,
                                      IntPredicate isValidNumberOfArguments, ServerContext context) {
        super(outputWriter, isValidNumberOfArguments, context);
    }

    @Override
    protected ServerContext getContext() {
        return (ServerContext) super.getContext();
    }
}
