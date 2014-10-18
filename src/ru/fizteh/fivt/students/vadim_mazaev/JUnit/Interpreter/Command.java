package ru.fizteh.fivt.students.vadim_mazaev.JUnit.Interpreter;

import java.util.function.BiConsumer;

public class Command {
    private String name;
    private int numberOfArgs;
    private BiConsumer<CommandConnector, String[]> callback;
    
    public Command(String name, int numberOfArgs, BiConsumer<CommandConnector, String[]> callback) {
        this.name = name;
        this.numberOfArgs = numberOfArgs;
        this.callback = callback;
    }
    
    public final String getName() {
        return name;
    }

    public final void execute(CommandConnector connector, String[] args) throws Exception {
        if (numberOfArgs != args.length) {
            throw new IllegalArgumentException(name + ": Incorrect number of arguments: "
                    + numberOfArgs + "expected, but " + args.length + " found.");
        }
        callback.accept(connector, args);
    }
}
