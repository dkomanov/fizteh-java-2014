package ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.DbManager;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.TableManager;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.exceptions.TableIsNotChosenException;

import java.io.IOException;
import java.util.Set;

public class UseCommand extends DbCommand {
    public UseCommand(final String[] args) {
        super("use", args);
    }

    @Override
    public void execute(final DbManager dbManager) throws IOException, TableIsNotChosenException {
    // DUMP BEFORE USE
        String tableToUse = args[0];
        TableManager currentTable = dbManager.getCurrentTable();
        Set<String> tableNames = dbManager.getTableNames();
        if (!tableNames.contains(tableToUse)) {
            msg = new String(tableToUse + " not exists");
        } else {
            if (currentTable != null && !currentTable.getTableName().equals(tableToUse)) {
                currentTable.dump();
            }
            dbManager.setCurrentTable(tableToUse);
            msg = new String("using " + tableToUse);
        }
    }
}
