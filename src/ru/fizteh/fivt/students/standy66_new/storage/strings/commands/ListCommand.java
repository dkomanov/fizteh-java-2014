package ru.fizteh.fivt.students.standy66_new.storage.strings.commands;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.standy66_new.exceptions.NoTableSelectedException;

import java.util.stream.Collectors;

/**
 * Created by astepanov on 20.10.14.
 */
public class ListCommand extends ContextualCommand {
    protected ListCommand(Context context) {
        super((x -> x == 1), context);
    }

    @Override
    public void run(String[] arguments) throws Exception {
        super.run(arguments);
        Table current = getContext().getCurrentTable();
        if (current == null) {
            throw new NoTableSelectedException("no table");
        }
        System.out.println(current.list().stream()
                .collect(Collectors.joining(", ")));
    }
}
