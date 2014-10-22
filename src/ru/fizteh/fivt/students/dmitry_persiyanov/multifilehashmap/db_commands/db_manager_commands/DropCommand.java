package ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands.db_manager_commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.DbManager;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.TableManager;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands.DbCommand;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class DropCommand extends DbManagerCommand {
    public DropCommand(final String[] args) {
        super("drop", args);
        NUM_OF_ARGS = 1;
        checkArgs();
    }

    @Override
    public void execute(final DbManager dbManager) throws IOException {
        String tableToDrop = args[0];
        if (!dbManager.containsTable(tableToDrop)) {
            msg = new String(tableToDrop + " not exists");
        } else {
            dbManager.dropTable(tableToDrop);
            msg = new String("dropped");
        }
    }
}
