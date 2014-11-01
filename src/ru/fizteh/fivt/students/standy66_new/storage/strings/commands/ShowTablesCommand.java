package ru.fizteh.fivt.students.standy66_new.storage.strings.commands;

import ru.fizteh.fivt.students.standy66_new.exceptions.WrongNumberOfArgumentsException;
import ru.fizteh.fivt.students.standy66_new.storage.strings.StringDatabase;

/**
 * Created by astepanov on 20.10.14.
 */
public class ShowTablesCommand extends ContextualCommand {

    protected ShowTablesCommand(Context context) {
        super((x -> x == 2), context);
    }

    @Override
    public void run(String[] arguments) throws Exception {
        super.run(arguments);
        if (!arguments[1].equals("tables")) {
            throw new WrongNumberOfArgumentsException("show must be followed by tables");
        }
        StringDatabase db = (StringDatabase) (getContext().getProvider());
        System.out.println("table_name      row_count");
        for (String name : db.listTableNames()) {
            System.out.printf("%s       %s\n", name, db.getTable(name).size());
        }

    }
}
