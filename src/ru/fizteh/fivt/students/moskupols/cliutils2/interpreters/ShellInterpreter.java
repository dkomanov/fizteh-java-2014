package ru.fizteh.fivt.students.moskupols.cliutils2.interpreters;

import ru.fizteh.fivt.students.moskupols.cliutils.StopProcessingException;
import ru.fizteh.fivt.students.moskupols.cliutils.UnknownCommandException;
import ru.fizteh.fivt.students.moskupols.cliutils2.CommandChooser;
import ru.fizteh.fivt.students.moskupols.cliutils2.commands.Command;
import ru.fizteh.fivt.students.moskupols.cliutils2.commands.FinalizerCommand;
import ru.fizteh.fivt.students.moskupols.cliutils2.exceptions.CommandExecutionException;

/**
 * Created by moskupols on 02.12.14.
 */
public abstract class ShellInterpreter implements Interpreter {
    protected boolean ignorable(String commandLine) {
        return commandLine.trim().isEmpty();
    }

    protected void runJob(Object context, CommandChooser chooser, String line)
            throws UnknownCommandException, CommandExecutionException, StopProcessingException {
        if (ignorable(line)) {
            return;
        }
        String[] args = line.split("\\s+");
        Command cmd = chooser.commandForArgs(args);
        if (cmd == null) {
            throw new UnknownCommandException(line);
        }
        cmd.perform(context, args);
        if (cmd instanceof FinalizerCommand) {
            throw new StopProcessingException();
        }
    }
}
