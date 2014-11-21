package ru.fizteh.fivt.students.standy66_new.storage.strings.commands;

import ru.fizteh.fivt.students.standy66_new.exceptions.TableNotExistsException;

import java.io.PrintWriter;

/**
 * Created by astepanov on 20.10.14.
 */
public class DropCommand extends ContextualCommand {
    protected DropCommand(PrintWriter writer, Context context) {
        super(writer, (x -> x == 2), context);
    }

    @Override
    public void execute(String... arguments) throws Exception {
        super.execute(arguments);
        if (getContext().getProvider().getTable(arguments[1]) == null) {
            throw new TableNotExistsException(String.format("%s not exists", arguments[1]));
        } else {
            getContext().getProvider().removeTable(arguments[1]);
            getOutputWriter().println("dropped");
        }
    }
}
