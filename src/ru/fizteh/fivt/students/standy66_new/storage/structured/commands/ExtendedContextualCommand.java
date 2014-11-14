package ru.fizteh.fivt.students.standy66_new.storage.structured.commands;

import ru.fizteh.fivt.students.standy66_new.storage.strings.commands.ContextualCommand;

import java.util.function.IntPredicate;

/**
 * Created by andrew on 14.11.14.
 */
public class ExtendedContextualCommand extends ContextualCommand {
    protected ExtendedContextualCommand(IntPredicate isValidNumberOfArguments, ExtendedContext context) {
        super(isValidNumberOfArguments, context);
    }

    public ExtendedContext getExtendedContext() {
        return (ExtendedContext) super.getContext();
    }
}
