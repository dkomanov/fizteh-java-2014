package ru.fizteh.fivt.students.vadim_mazaev.Interpreter;

import java.util.function.BiConsumer;

public class Command {
    private String name;
    private int numberOfArgs;
    private BiConsumer<Object, String[]> callback;
    
    public Command(String name, int numberOfArgs, BiConsumer<Object, String[]> callback) {
        this.name = name;
        this.numberOfArgs = numberOfArgs;
        this.callback = callback;
    }
    
    public final String getName() {
        return name;
    }

    public final void execute(Object connector, String[] args) throws Exception {
        if (numberOfArgs != args.length) {
            throw new StopLineInterpretationException(name + ": Incorrect number of arguments: "
                    + numberOfArgs + " expected, but " + args.length + " found.");
        }
        callback.accept(connector, args);
    }
}
