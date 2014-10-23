package ru.fizteh.fivt.students.anastasia_ermolaeva.multifilehashmap.util;

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
                        final String[] arguments) throws ExitException {
        if (arguments.length != numArguments) {
            System.err.println("Invalid number of arguments: "
                    + numArguments
                    + " expected, "
                    + arguments.length
                    + " found.");
        } else {
            callback.accept(tableState, arguments);
        }
    }
}
