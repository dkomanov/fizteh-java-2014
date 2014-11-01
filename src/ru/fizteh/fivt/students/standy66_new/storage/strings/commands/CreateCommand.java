package ru.fizteh.fivt.students.standy66_new.storage.strings.commands;

import ru.fizteh.fivt.students.standy66_new.exceptions.TableExistsException;

/**
 * Created by astepanov on 20.10.14.
 */
public class CreateCommand extends ContextualCommand {

    protected CreateCommand(Context context) {
        super((x -> x == 2), context);
    }

    @Override
    public void run(String[] arguments) throws Exception {
        super.run(arguments);
        if (getContext().getProvider().createTable(arguments[1]) != null) {
            System.out.println("created");
        } else {
            throw new TableExistsException(String.format("%s exists", arguments[1]));
        }
    }
}
