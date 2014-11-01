package ru.fizteh.fivt.students.standy66_new.storage.strings.commands;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.standy66_new.exceptions.NoTableSelectedException;

/**
 * Created by astepanov on 20.10.14.
 */
public class PutCommand extends ContextualCommand {

    protected PutCommand(Context context) {
        super((x -> x == 3), context);
    }

    @Override
    public void run(String[] arguments) throws Exception {
        super.run(arguments);
        Table current = getContext().getCurrentTable();
        if (current == null) {
            throw new NoTableSelectedException("no table");
        }
        String currentValue = current.get(arguments[1]);
        if (currentValue == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(currentValue);
        }
        current.put(arguments[1], arguments[2]);

    }
}
