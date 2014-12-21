package ru.fizteh.fivt.students.standy66_new.storage.structured.commands;

import java.io.PrintWriter;

/**
 * Created by astepanov on 20.10.14.
 */
public class StructuredDrop extends ExtendedContextualCommand {
    protected StructuredDrop(PrintWriter writer, ExtendedContext context) {
        super(writer, (x -> x == 2), context);
    }

    @Override
    public void execute(String... arguments) throws Exception {
        super.execute(arguments);
        getContext().getStructuredDatabase().removeTable(arguments[1]);
        getContext().setCurrentStructuredTable(null);
        getOutputWriter().println("dropped");
    }
}
