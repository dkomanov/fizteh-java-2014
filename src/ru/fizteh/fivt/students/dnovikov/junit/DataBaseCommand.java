package ru.fizteh.fivt.students.dnovikov.junit;

import ru.fizteh.fivt.students.dnovikov.junit.Exceptions.WrongNumberOfArgumentsException;
import ru.fizteh.fivt.students.dnovikov.junit.Interpreter.Command;
import ru.fizteh.fivt.students.dnovikov.junit.Interpreter.InterpreterState;

import java.util.function.BiConsumer;

public class DataBaseCommand implements Command {
    private BiConsumer<InterpreterState, String[]> callback;
    private String name;
    private int numOfArguments;

    public DataBaseCommand(String name, int numOfArguments, BiConsumer<InterpreterState, String[]> callback) {
        this.name = name;
        this.numOfArguments = numOfArguments;
        this.callback = callback;
    }

    public String getName() {
        return name;
    }

    public void invoke(InterpreterState state, String[] args) throws WrongNumberOfArgumentsException {
        if (args.length != numOfArguments) {
            throw new WrongNumberOfArgumentsException(getName());
        }
        callback.accept(state, args);
    }
}
