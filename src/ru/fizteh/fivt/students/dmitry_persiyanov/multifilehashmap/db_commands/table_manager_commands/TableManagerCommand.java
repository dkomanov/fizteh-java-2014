package ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands.table_manager_commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.TableManager;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands.DbCommand;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.exceptions.TableIsNotChosenException;

import java.io.IOException;

public abstract class TableManagerCommand extends DbCommand {
    public TableManagerCommand(final String name, final String[] args) {
        super(name, args);
    }

    public abstract void execute(final TableManager tableManager) throws IOException, TableIsNotChosenException;
}
