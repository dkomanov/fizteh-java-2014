package ru.fizteh.fivt.students.AlexanderKhalyapov.JUnit;

import java.util.function.BiConsumer;

public class Command {
    private String name;
    private int numArguments;
    private BiConsumer<TableState, String[]> callback;

    public Command(final String name, final int numArguments,
                   final BiConsumer<TableState, String[]> callback) {
        this.name = name;
        this.numArguments = numArguments;
        this.callback = callback;
    }

    public final String getName() {
        return name;
    }

    public final void execute(final TableState tableState,
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
