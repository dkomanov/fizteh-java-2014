package ru.fizteh.fivt.students.anastasia_ermolaeva.junit.util;

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
                              final String[] arguments) throws ExitException,
            IllegalNumberOfArgumentsException {
        if (arguments.length != numArguments) {
            String errMessage = "Invalid number of arguments: "
                    + numArguments
                    + " expected, "
                    + arguments.length
                    + " found.";
            throw new IllegalNumberOfArgumentsException(errMessage);
        } else {
            callback.accept(tableState, arguments);
        }
    }
}
