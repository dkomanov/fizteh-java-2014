package ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.DbManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;

public class CreateCommand extends DbCommand {
    public CreateCommand(final String[] args) {
        super("list", args);
        NUM_OF_ARGS = 1;
        checkArgs();
    }

    @Override
    public void execute(final DbManager dbManager) throws IOException {
        String tableToCreate = args[0];
        Set<String> tableNames = dbManager.getTableNames();
        File rootDir = dbManager.getRootDir();
        if (!tableNames.contains(tableToCreate)) {
            Files.createDirectory(dbManager.getTablePath(tableToCreate));
            tableNames.add(tableToCreate);
            msg = new String("created");
        } else {
            msg = new String(tableToCreate + " exists");
        }
    }
}
