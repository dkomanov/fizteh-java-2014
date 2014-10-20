package ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.DbManager;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ShowTablesCommand extends DbCommand {
    public ShowTablesCommand(final String[] args) {
        super("show tables", args);
    }

    @Override
    public void execute(final DbManager dbManager) throws IOException {
        Set<String> tableNames = dbManager.getTableNames();
        msg = String.join(", ", tableNames);
    }
}
