package ru.fizteh.fivt.students.standy66_new.storage.structured.commands;

import ru.fizteh.fivt.students.standy66_new.exceptions.TableExistsException;
import ru.fizteh.fivt.students.standy66_new.utility.ClassUtility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrew on 14.11.14.
 */
public class StructuredCreate extends ExtendedContextualCommand {
    protected StructuredCreate(ExtendedContext context) {
        super((x -> x > 2), context);
    }

    @Override
    public void run(String[] arguments) throws Exception {
        super.run(arguments);
        List<Class<?>> columnClasses = new ArrayList<>();
        for (int i = 2; i < arguments.length; i++) {
            columnClasses.add(ClassUtility.forName(arguments[i]));
        }
        if (getExtendedContext().getStructuredDatabase().createTable(arguments[1], columnClasses) != null) {
            System.out.println("created");
        } else {
            throw new TableExistsException(String.format("%s exists", arguments[1]));
        }
    }
}
