package ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands.db_manager_commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.DbManager;

import java.io.IOException;

public class DropCommand extends DbManagerCommand {
    public DropCommand(final String[] args) {
        super("drop", args);
        numOfArgs = 1;
        checkArgs();
    }

    @Override
    public void execute(final DbManager dbManager) throws IOException {
        String tableToDrop = args[0];
        if (!dbManager.containsTable(tableToDrop)) {
            msg = tableToDrop + " not exists";
        } else {
            dbManager.dropTable(tableToDrop);
            msg = "dropped";
        }
    }
}
