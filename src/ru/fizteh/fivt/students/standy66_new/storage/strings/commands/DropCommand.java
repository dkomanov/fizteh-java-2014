package ru.fizteh.fivt.students.standy66_new.storage.strings.commands;

import ru.fizteh.fivt.students.standy66_new.exceptions.TableNotExistsException;

/**
 * Created by astepanov on 20.10.14.
 */
public class DropCommand extends ContextualCommand {
    protected DropCommand(Context context) {
        super((x -> x == 2), context);
    }

    @Override
    public void run(String[] arguments) throws Exception {
        super.run(arguments);
        if (getContext().getProvider().getTable(arguments[1]) == null) {
            throw new TableNotExistsException(String.format("%s not exists", arguments[1]));
        } else {
            getContext().getProvider().removeTable(arguments[1]);
            System.out.println("dropped");
        }
    }
}
