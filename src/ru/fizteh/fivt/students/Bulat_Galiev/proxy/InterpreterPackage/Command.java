package ru.fizteh.fivt.students.Bulat_Galiev.proxy.InterpreterPackage;

import java.util.function.BiConsumer;

import ru.fizteh.fivt.storage.structured.TableProvider;

public class Command {
    private String name;
    private int numberOfArgs;
    protected BiConsumer<TableProvider, String[]> callback;

    public Command(final String name, final int numberOfArgs,
            final BiConsumer<TableProvider, String[]> callback) {
        this.name = name;
        this.numberOfArgs = numberOfArgs;
        this.callback = callback;
    }

    public final String getName() {
        return name;
    }

    public void execute(final TableProvider connector, final String[] args)
            throws Exception {
        check(args);
        callback.accept(connector, args);
    }
    
    public final void check(String[] args)
    {
        if (numberOfArgs + 1 != args.length) {
            throw new StopInterpretationException(name
                    + ": Incorrect number of arguments: " + numberOfArgs
                    + " expected, but " + (args.length - 1) + " found");
        }
    }
}
