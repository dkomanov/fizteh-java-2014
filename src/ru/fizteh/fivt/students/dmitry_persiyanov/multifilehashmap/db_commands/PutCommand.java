package ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.DbManager;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.TableManager;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.exceptions.TableIsNotChosenException;

import java.io.IOException;

public class PutCommand extends DbCommand {
    public PutCommand(final String[] args) {
        super("put", args);
        NUM_OF_ARGS = 2;
        checkArgs();
    }

    @Override
    public void execute(final DbManager dbManager) throws IOException, TableIsNotChosenException {
        if (dbManager.getCurrentTable() == null) {
            throw new TableIsNotChosenException();
        }
        String key = args[0];
        String value = args[1];
        TableManager currentTable = dbManager.getCurrentTable();
        String oldValue = currentTable.getTableHashMap().get(TableManager.keyValueDirNum(key))
                .get(TableManager.keyValueFileNum(key)).put(key, value);
        if (oldValue == null) {
            msg = new String("new");
        } else {
            msg = new String("overwrite" + System.lineSeparator() + oldValue);
        }
    }
}
