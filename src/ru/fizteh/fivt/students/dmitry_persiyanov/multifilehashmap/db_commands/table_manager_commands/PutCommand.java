package ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands.table_manager_commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.TableManager;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.exceptions.TableIsNotChosenException;

import java.io.IOException;

public class PutCommand extends TableManagerCommand {
    public PutCommand(final String[] args) {
        super("put", args);
        numOfArgs = 2;
        checkArgs();
    }

    @Override
    public void execute(final TableManager tableManager) throws IOException, TableIsNotChosenException {
        if (tableManager == null) {
            throw new TableIsNotChosenException();
        }
        String key = args[0];
        String value = args[1];
        String oldValue = tableManager.put(key, value);
        if (oldValue == null) {
            msg = new String("new");
        } else {
            msg = new String("overwrite" + System.lineSeparator() + oldValue);
        }
    }
}
