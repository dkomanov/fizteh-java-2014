package ru.fizteh.fivt.students.standy66_new.storage.strings.commands;

import ru.fizteh.fivt.students.standy66_new.commands.Command;

import java.util.function.IntPredicate;

/**
 * Created by astepanov on 20.10.14.
 */
public class ContextualCommand extends Command {
    private Context context;

    protected ContextualCommand(IntPredicate isValidNumberOfArgumentsPredicate, Context context) {
        super(isValidNumberOfArgumentsPredicate);
        this.context = context;
    }

    public Context getContext() {
        return context;
    }
}
