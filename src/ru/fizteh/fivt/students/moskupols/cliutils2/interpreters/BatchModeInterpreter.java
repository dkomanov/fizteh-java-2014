package ru.fizteh.fivt.students.moskupols.cliutils2.interpreters;

import ru.fizteh.fivt.students.moskupols.cliutils.StopProcessingException;
import ru.fizteh.fivt.students.moskupols.cliutils.UnknownCommandException;
import ru.fizteh.fivt.students.moskupols.cliutils2.CommandChooser;
import ru.fizteh.fivt.students.moskupols.cliutils2.exceptions.CommandExecutionException;

/**
 * Created by moskupols on 03.12.14.
 */
public class BatchModeInterpreter extends Interpreter {
    private final String[] splittedCommands;
    private final Object context;
    private final CommandChooser chooser;

    public BatchModeInterpreter(Object context, CommandChooser chooser, String[] args) {
        this.context = context;
        this.chooser = chooser;
        StringBuilder joiner = new StringBuilder();
        for (String s : args) {
            joiner.append(s);
            joiner.append(' ');
        }
        splittedCommands = joiner.toString().split(";");
    }

    @Override
    public void interpret() throws CommandExecutionException, UnknownCommandException {
        for (String s : splittedCommands) {
            boolean needed = true;
            try {
                runJob(context, chooser, s);
            } catch (StopProcessingException e) {
                needed = false;
            }
            if (!needed) {
                break;
            }
        }
    }
}
