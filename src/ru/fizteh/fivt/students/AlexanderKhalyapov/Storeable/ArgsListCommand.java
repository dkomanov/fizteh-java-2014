package ru.fizteh.fivt.students.AlexanderKhalyapov.Storeable;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class ArgsListCommand extends Command {
    private Function<String[], String[]> argumentsHandler;
    public ArgsListCommand(String name, int numArguments,
                           BiConsumer<Object, String[]> callback, Function<String[], String[]> handler) {
        super(name, numArguments, callback);
        argumentsHandler = handler;
    }
    @Override
    public final void execute(final Object tableState,
                              final String[] arguments) {
        try {
            String[] newArguments = argumentsHandler.apply(arguments);
            callback.accept(tableState, newArguments);
        } catch (IllegalArgumentException e) {
            throw new IllegalCommandException(e.getMessage());
        }
    }
}
