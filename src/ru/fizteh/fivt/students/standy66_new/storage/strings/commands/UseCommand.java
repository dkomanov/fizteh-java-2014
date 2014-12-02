package ru.fizteh.fivt.students.standy66_new.storage.strings.commands;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.standy66_new.exceptions.TableNotExistsException;
import ru.fizteh.fivt.students.standy66_new.exceptions.UncommitedChangesException;
import ru.fizteh.fivt.students.standy66_new.storage.strings.StringTable;

import java.io.PrintWriter;

/**
 * Created by astepanov on 20.10.14.
 */
public class UseCommand extends ContextualCommand {

    public static final boolean WARN_OF_UNSAVED_CHANGES = System.getProperty("warn_unsaved") != null;

    protected UseCommand(PrintWriter writer, Context context) {
        super(writer, (x -> x == 2), context);
    }

    @Override
    public void execute(String... arguments) throws Exception {
        super.execute(arguments);
        Table newTable = getContext().getProvider().getTable(arguments[1]);
        StringTable currentTable = (StringTable) getContext().getCurrentTable();
        if (newTable == null) {
            throw new TableNotExistsException(String.format("%s not exists", arguments[1]));
        } else {
            if (WARN_OF_UNSAVED_CHANGES) {
                if ((currentTable != null) && (currentTable.unsavedChangesCount() > 0)) {
                    throw new UncommitedChangesException(String.format("%d unsaved changes",
                            currentTable.unsavedChangesCount()));
                }
            }
            getContext().setCurrentTable(newTable);
            getOutputWriter().printf("using %s%n", arguments[1]);
        }
    }
}
