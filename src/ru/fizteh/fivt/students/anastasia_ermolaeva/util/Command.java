package ru.fizteh.fivt.students.anastasia_ermolaeva.util;

import ru.fizteh.fivt.students.anastasia_ermolaeva.util.exceptions.IllegalCommandException;

import java.util.function.BiConsumer;

public class Command {
    protected String name;
    protected int numArguments;
    protected BiConsumer<Object, String[]> callback;

    public Command(final String name, final int numArguments,
                   final BiConsumer<Object, String[]> callback) {
        this.name = name;
        this.numArguments = numArguments;
        this.callback = callback;
    }

    public String getName() {
        return name;
    }

    public void execute(final Object tableState,
                        final String[] arguments) {
        if (arguments.length != numArguments) {
            String errMessage = getName() + ": invalid number of arguments: "
                    + (numArguments - 1)
                    + " expected, "
                    + (arguments.length - 1)
                    + " found.";
            throw new IllegalCommandException(errMessage);
        } else {
            callback.accept(tableState, arguments);
        }
    }
}
