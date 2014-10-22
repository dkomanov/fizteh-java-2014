package ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands.table_manager_commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.DbManager;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.TableManager;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands.DbCommand;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.exceptions.TableIsNotChosenException;

import java.io.IOException;

public class PutCommand extends TableManagerCommand {
    public PutCommand(final String[] args) {
        super("put", args);
        NUM_OF_ARGS = 2;
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
