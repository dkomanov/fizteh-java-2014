package ru.fizteh.fivt.students.standy66_new.storage.strings.commands;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.standy66_new.exceptions.TableNotExistsException;
import ru.fizteh.fivt.students.standy66_new.exceptions.UnsavedChangesException;
import ru.fizteh.fivt.students.standy66_new.storage.strings.StringTable;

/**
 * Created by astepanov on 20.10.14.
 */
public class UseCommand extends ContextualCommand {
    protected UseCommand(Context context) {
        super((x -> x == 2), context);
    }

    @Override
    public void run(String[] arguments) throws Exception {
        super.run(arguments);
        Table newTable = getContext().getProvider().getTable(arguments[1]);
        StringTable currentTable = (StringTable)getContext().getCurrentTable();
        if (newTable == null) {
            throw new TableNotExistsException(String.format("%s not exists", arguments[1]));
        } else {
            if (System.getProperty("junit") != null) {
                if (currentTable != null && currentTable.unsavedChangesCount() > 0) {
                    throw new UnsavedChangesException(String.format("%d unsaved changes", currentTable.unsavedChangesCount()));
                }
            }
            getContext().setCurrentTable(newTable);
            System.out.printf("using %s\n", arguments[1]);
        }
    }
}
