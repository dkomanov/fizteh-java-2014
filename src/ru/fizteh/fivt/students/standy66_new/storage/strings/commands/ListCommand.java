package ru.fizteh.fivt.students.standy66_new.storage.strings.commands;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.standy66_new.exceptions.NoTableSelectedException;

import java.io.PrintWriter;
import java.util.stream.Collectors;

/**
 * Created by astepanov on 20.10.14.
 */
public class ListCommand extends ContextualCommand {
    protected ListCommand(PrintWriter writer, Context context) {
        super(writer, (x -> x == 1), context);
    }

    @Override
    public void execute(String... arguments) throws Exception {
        super.execute(arguments);
        Table current = getContext().getCurrentTable();
        if (current == null) {
            throw new NoTableSelectedException();
        }
        out.println(current.list().stream()
                .collect(Collectors.joining(", ")));
    }
}
