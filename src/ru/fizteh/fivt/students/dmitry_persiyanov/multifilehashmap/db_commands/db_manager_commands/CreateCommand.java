package ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands.db_manager_commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.DbManager;

import java.io.IOException;

public class CreateCommand extends DbManagerCommand {
    public CreateCommand(final String[] args) {
        super("list", args);
        numOfArgs = 1;
        checkArgs();
    }

    @Override
    public void execute(final DbManager dbManager) throws IOException {
        String tableToCreate = args[0];
        if (!dbManager.containsTable(tableToCreate)) {
            dbManager.createTable(tableToCreate);
            msg = "created";
        } else {
            msg = tableToCreate + " exists";
        }
    }
}
