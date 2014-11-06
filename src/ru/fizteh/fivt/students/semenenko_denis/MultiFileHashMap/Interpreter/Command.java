package ru.fizteh.fivt.students.semenenko_denis.MultiFileHashMap.Interpreter;

import java.util.function.BiConsumer;

public class Command {
    private String name;
    private int numArguments;
    private BiConsumer<InterpreterState, String[]> callback;

    public Command(String name, int numArguments, BiConsumer<InterpreterState, String[]> callback) {
        this.name = name;
        this.numArguments = numArguments;
        this.callback = callback;
    }

    public String getName() {
        return name;
    }

    public void execute(InterpreterState interpreterState, String[] params) {
        if (params.length != numArguments) {
            Utils.interpreterError("Invalid number of arguments: " + numArguments + " expected, " + params.length
                    + " found.");
        } else {
            callback.accept(interpreterState, params);
        }
    }
}