package ru.fizteh.fivt.students.AlexanderKhalyapov.Proxy;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class DatabaseCommand implements Command {
    protected String name;
    protected int numArguments;
    protected BiConsumer<DataBase, String[]> callback;
    protected DataBase dataBase;
    protected Function<String[], String[]> argumentsHandler;

    public DatabaseCommand(DataBase dataBase, String name,
                           int numArguments, BiConsumer<DataBase, String[]> callback) {
        this.dataBase = dataBase;
        this.name = name;
        this.numArguments = numArguments;
        this.callback = callback;
    }

    public DatabaseCommand(DataBase dataBase, String name, int numArguments,
                           BiConsumer<DataBase, String[]> callback, Function<String[], String[]> handler) {
        this(dataBase, name, numArguments, callback);
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
        callback.accept(dataBase, checkAndCorrectArguments(arguments));
    }
}
