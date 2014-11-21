package ru.fizteh.fivt.students.standy66_new.storage.strings.commands;

import ru.fizteh.fivt.students.standy66_new.commands.Command;

import java.io.PrintWriter;
import java.util.function.IntPredicate;

/**
 * Created by astepanov on 20.10.14.
 */
public class ContextualCommand extends Command {
    private final Context context;

    protected ContextualCommand(PrintWriter writer, IntPredicate isValidNumberOfArgumentsPredicate, Context context) {
        super(writer, isValidNumberOfArgumentsPredicate);
        this.context = context;
    }

    public Context getContext() {
        return context;
    }
}
