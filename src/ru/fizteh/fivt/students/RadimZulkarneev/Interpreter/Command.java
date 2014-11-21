package ru.fizteh.fivt.students.RadimZulkarneev.Interpreter;

import java.util.function.BiConsumer;
public class Command {
    private String name;
    private int numberOfArguments;
    private BiConsumer<InterpreterState, String[]> callback;

    public Command(String name, int numOfArguments, BiConsumer<InterpreterState, String[]> callback) {
        this.name = name;
        this.numberOfArguments = numOfArguments;
        this.callback = callback;
    }

    public void execute(InterpreterState state, String[] params) {
        if (params.length != numberOfArguments) {
            throw new IllegalArgumentException("Invalid number of arguments: "
        + numberOfArguments + " expected, " + params.length
                    + " found.");
        } else {
            callback.accept(state, params);
        }
    }
    public String getName() {
        return name;
    }
}
