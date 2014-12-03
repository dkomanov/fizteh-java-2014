package ru.fizteh.fivt.students.standy66_new.storage.strings.commands;

import ru.fizteh.fivt.students.standy66_new.exceptions.WrongNumberOfArgumentsException;
import ru.fizteh.fivt.students.standy66_new.storage.strings.StringDatabase;

import java.io.PrintWriter;

/**
 * Created by astepanov on 20.10.14.
 */
public class ShowTablesCommand extends ContextualCommand {

    protected ShowTablesCommand(PrintWriter writer, Context context) {
        super(writer, (x -> x == 2), context);
    }

    @Override
    public void execute(String... arguments) throws Exception {
        super.execute(arguments);

        if (!"tables".equals(arguments[1])) {
            throw new WrongNumberOfArgumentsException("show must be followed by tables");
        }
        StringDatabase db = (StringDatabase) (getContext().getProvider());
        getOutputWriter().println("table_name      row_count");
        for (String name : db.listTableNames()) {
            getOutputWriter().printf("%s       %s%n", name, db.getTable(name).size());
        }

    }
}
