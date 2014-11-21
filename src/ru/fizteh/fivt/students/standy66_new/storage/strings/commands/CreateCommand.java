package ru.fizteh.fivt.students.standy66_new.storage.strings.commands;

import ru.fizteh.fivt.students.standy66_new.exceptions.TableExistsException;

import java.io.PrintWriter;

/**
 * Created by astepanov on 20.10.14.
 */
public class CreateCommand extends ContextualCommand {

    protected CreateCommand(PrintWriter writer, Context context) {
        super(writer, (x -> x == 2), context);
    }

    @Override
    public void execute(String... arguments) throws Exception {
        super.execute(arguments);
        if (getContext().getProvider().createTable(arguments[1]) != null) {
            out.println("created");
        } else {
            throw new TableExistsException(String.format("%s exists", arguments[1]));
        }
    }
}
