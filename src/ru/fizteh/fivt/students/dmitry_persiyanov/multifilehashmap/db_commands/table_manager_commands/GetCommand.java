package ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands.table_manager_commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.TableManager;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.exceptions.TableIsNotChosenException;

import java.io.IOException;

public class GetCommand extends TableManagerCommand {
    public GetCommand(final String[] args) {
        super("get", args);
        numOfArgs = 1;
        checkArgs();
    }

    @Override
    public void execute(final TableManager tableManager) throws IOException, TableIsNotChosenException {
        if (tableManager == null) {
            throw new TableIsNotChosenException();
        }
        String key = args[0];
        String value = tableManager.get(key);
        if (value == null) {
            msg = new String("not found");
        } else {
            msg = new String("found" + System.lineSeparator() + value);
        }
    }
}
