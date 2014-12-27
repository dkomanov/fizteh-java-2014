package ru.fizteh.fivt.students.AlexanderKhalyapov.Telnet;

import java.util.function.BiConsumer;

public class Command {
    private String name;
    private int numberOfArgs;
    private BiConsumer<Object, String[]> callback;

    public Command(String name, int numberOfArgs,
                   BiConsumer<Object, String[]> callback) {
        this.name = name;
        this.numberOfArgs = numberOfArgs;
        this.callback = callback;
    }

    public final String getName() {
        return name;
    }

    public final void execute(Object connector, String[] args) {
        if (numberOfArgs != args.length) {
            String message = name + ": Incorrect number of arguments: "
                    + numberOfArgs + " expected, but found " + args.length;
            if (args.length > 0) {
                message += ": '" + String.join("', '", args) + "'";
            }
            throw new StopLineInterpretationException(message);
        }
        callback.accept(connector, args);
    }
}
