package ru.fizteh.fivt.students.anastasia_ermolaeva.proxy.commands;

import ru.fizteh.fivt.students.anastasia_ermolaeva.proxy.TableHolder;
import ru.fizteh.fivt.students.anastasia_ermolaeva.util.exceptions.IllegalCommandException;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class DatabaseCommand implements Command {
    protected String name;
    protected int numArguments;
    protected BiConsumer<TableHolder, String[]> callback;
    protected TableHolder tableHolder;
    protected Function<String[],String[]> argumentsHandler;

    public DatabaseCommand(TableHolder tableHolder, String name, int numArguments, BiConsumer<TableHolder, String[]> callback) {
        this.tableHolder = tableHolder;
        this.name = name;
        this.numArguments = numArguments;
        this.callback = callback;
    }

    public DatabaseCommand(TableHolder tableHolder, String name, int numArguments,
                           BiConsumer<TableHolder, String[]> callback, Function<String[],String[]> handler) {
        this(tableHolder, name, numArguments, callback);
        argumentsHandler = handler;
    }

    @Override
    public String getName() {
        return name;
    }


    public String[] checkAndCorrectArguments(String[] arguments) {
        String[] newArguments = arguments;
        if (argumentsHandler != null) {
            newArguments = argumentsHandler.apply(arguments);
        }
        if (newArguments.length != numArguments) {
            String errMessage = getName() + ": invalid number of arguments: "
                    + (numArguments - 1)
                    + " expected, "
                    + (arguments.length - 1)
                    + " found.";
            throw new IllegalCommandException(errMessage);
        }
        return newArguments;
    }

    @Override
    public void run(final String[] arguments) {
        callback.accept(tableHolder, checkAndCorrectArguments(arguments));
    }
}
