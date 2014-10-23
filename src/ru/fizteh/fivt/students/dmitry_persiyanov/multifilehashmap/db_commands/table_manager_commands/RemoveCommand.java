package ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands.table_manager_commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.TableManager;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.exceptions.TableIsNotChosenException;

import java.io.IOException;

public class RemoveCommand extends TableManagerCommand {
    public RemoveCommand(final String[] args) {
        super("remove", args);
        numOfArgs = 1;
        checkArgs();
    }

    @Override
    public void execute(final TableManager tableManager) throws IOException, TableIsNotChosenException {
        if (tableManager == null) {
            throw new TableIsNotChosenException();
        }
        String key = args[0];
        String value = tableManager.remove(key);
        if (value == null) {
            msg = new String("not found");
        } else {
            msg = new String("removed");
        }
    }
}
