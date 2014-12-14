package ru.fizteh.fivt.students.standy66_new.storage.strings.commands;

import java.io.PrintWriter;
import java.util.function.IntPredicate;

/**
 * Created by astepanov on 20.10.14.
 */
public class ContextualCommand extends ru.fizteh.fivt.students.standy66_new.commands.ContextualCommand {

    protected ContextualCommand(PrintWriter writer, IntPredicate isValidNumberOfArguments, Context context) {
        super(writer, isValidNumberOfArguments, context);
    }

    @Override
    protected Context getContext() {
        return (Context) super.getContext();
    }
}
