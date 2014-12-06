package ru.fizteh.fivt.students.dnovikov.storeable.Interpreter;

import ru.fizteh.fivt.students.dnovikov.storeable.DataBaseProvider;
import ru.fizteh.fivt.students.dnovikov.storeable.Exceptions.WrongNumberOfArgumentsException;

import java.util.function.BiConsumer;

public class Command {
    private BiConsumer<DataBaseProvider, String[]> callback;
    private String name;
    private int numOfArguments;

    public Command(String name, int numOfArguments, BiConsumer<DataBaseProvider, String[]> callback) {
        this.name = name;
        this.callback = callback;
        this.numOfArguments = numOfArguments;
    }

    public String getName() {
        return name;
    }

    public void invoke(DataBaseProvider dbConnector, String[] args) throws WrongNumberOfArgumentsException {
        if (numOfArguments >= 0) {
            if (args.length != numOfArguments) {
                throw new WrongNumberOfArgumentsException(getName());
            }
        }
        callback.accept(dbConnector, args);
    }


}
