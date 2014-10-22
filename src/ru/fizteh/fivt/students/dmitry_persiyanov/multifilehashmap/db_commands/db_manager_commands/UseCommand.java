package ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands.db_manager_commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.DbManager;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.TableManager;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands.DbCommand;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.exceptions.TableIsNotChosenException;

import java.io.IOException;
import java.util.Set;

public class UseCommand extends DbManagerCommand {
    public UseCommand(final String[] args) {
        super("use", args);
    }

    @Override
    public void execute(final DbManager dbManager) throws IOException {
        String tableToUse = args[0];
        if (!dbManager.containsTable(tableToUse)) {
            msg = new String(tableToUse + " not exists");
        } else {
            dbManager.useTable(tableToUse);
            msg = new String("using " + tableToUse);
        }
    }
}
