package ru.fizteh.fivt.students.standy66_new.storage.structured.commands;

import ru.fizteh.fivt.students.standy66_new.exceptions.TableExistsException;
import ru.fizteh.fivt.students.standy66_new.utility.ClassUtility;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andrew on 14.11.14.
 */
public class StructuredCreate extends ExtendedContextualCommand {
    protected StructuredCreate(PrintWriter writer, ExtendedContext context) {
        super(writer, (x -> x > 2), context);
    }

    @Override
    public void execute(String... arguments) throws Exception {
        super.execute(arguments);
        List<Class<?>> columnClasses = new ArrayList<>();
        if (!arguments[2].startsWith("(") || !arguments[arguments.length - 1].endsWith(")")) {
            throw new IllegalArgumentException("usage: create tablename (type1 type2 ... typeN)");
        }
        for (int i = 2; i < arguments.length; i++) {
            String type = arguments[i];
            if (i == 2) {
                type = type.substring(1);
            }
            if (i == arguments.length - 1) {
                type = type.substring(0, type.length() - 1);
            }
            columnClasses.add(ClassUtility.forName(type));
        }
        if (getContext().getStructuredDatabase().createTable(arguments[1], columnClasses) != null) {
            getOutputWriter().println("created");
        } else {
            throw new TableExistsException(String.format("%s exists", arguments[1]));
        }
    }
}
