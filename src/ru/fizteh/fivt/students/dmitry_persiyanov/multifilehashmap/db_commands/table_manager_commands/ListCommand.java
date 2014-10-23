package ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands.table_manager_commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.TableManager;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.exceptions.TableIsNotChosenException;

import java.io.IOException;
import java.util.*;

public class ListCommand extends TableManagerCommand {
    public ListCommand(final String[] args) {
        super("list", args);
        numOfArgs = 0;
        checkArgs();
    }

    @Override
    public void execute(final TableManager tableManager) throws IOException, TableIsNotChosenException {
        if (tableManager == null) {
            throw new TableIsNotChosenException();
        }
        List<String> allKeys = tableManager.list();
        if (allKeys.size() != 0) {
            msg = String.join(", ", allKeys);
        } else {
            msg = new String("");
        }
    }
}
