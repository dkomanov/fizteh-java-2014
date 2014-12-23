package ru.fizteh.fivt.students.dnovikov.storeable;

import ru.fizteh.fivt.students.dnovikov.storeable.Exceptions.WrongNumberOfArgumentsException;
import ru.fizteh.fivt.students.dnovikov.storeable.Interpreter.Command;
import ru.fizteh.fivt.students.dnovikov.storeable.Interpreter.InterpreterState;

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
        if (numOfArguments != -1) {
            if (args.length != numOfArguments) {
                throw new WrongNumberOfArgumentsException(getName());
            }
        }
        callback.accept(state, args);
    }

}
