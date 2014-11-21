package ru.fizteh.fivt.students.standy66_new.storage.strings.commands;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.standy66_new.exceptions.NoTableSelectedException;

import java.io.PrintWriter;

/**
 * Created by andrew on 01.11.14.
 */
public class CommitCommand extends ContextualCommand {
    public CommitCommand(PrintWriter writer, Context context) {
        super(writer, (x -> x == 1), context);
    }

    @Override
    public void execute(String... arguments) throws Exception {
        super.execute(arguments);

        Table currentTable = getContext().getCurrentTable();
        if (currentTable == null) {
            throw new NoTableSelectedException();
        }
        currentTable.commit();
    }
}
