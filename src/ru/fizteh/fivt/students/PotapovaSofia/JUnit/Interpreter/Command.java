package ru.fizteh.fivt.students.PotapovaSofia.JUnit.Interpreter;

import ru.fizteh.fivt.students.PotapovaSofia.JUnit.TableState;

import java.io.IOException;
import java.util.function.BiConsumer;

    public class Command {
    private String name;
    private int numArgs;
    private BiConsumer<TableState, String[]> callback;

    public Command(String name, int numArgs, BiConsumer<TableState, String[]> callback) {
        this.name = name;
        this.numArgs = numArgs;
        this.callback = callback;
    }

    public String getName() {
        return name;
    }

    public void execute(TableState state, String[] params) {
        if (params.length != numArgs) {
            Utils.interpreterError("Invalid number of arguments: " + numArgs + " expected, " + params.length
                    + " found.");
        } else {
            callback.accept(state, params);
        }
    }
}