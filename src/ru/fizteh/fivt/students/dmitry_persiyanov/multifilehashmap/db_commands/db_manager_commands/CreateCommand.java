package ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands.db_manager_commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.DbManager;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands.DbCommand;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;

public class CreateCommand extends DbManagerCommand {
    public CreateCommand(final String[] args) {
        super("list", args);
        NUM_OF_ARGS = 1;
        checkArgs();
    }

    @Override
    public void execute(final DbManager dbManager) throws IOException {
        String tableToCreate = args[0];
        if (!dbManager.containsTable(tableToCreate)) {
            dbManager.createTable(tableToCreate);
            msg = new String("created");
        } else {
            msg = new String(tableToCreate + " exists");
        }
    }
}
