package ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.DbManager;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.TableManager;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.exceptions.TableIsNotChosenException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class GetCommand extends DbCommand {
    public GetCommand(final String[] args) {
        super("get", args);
        NUM_OF_ARGS = 1;
        checkArgs();
    }

    @Override
    public void execute(final DbManager dbManager) throws IOException, TableIsNotChosenException {
        if (dbManager.getCurrentTable() == null) {
            throw new TableIsNotChosenException();
        }
        TableManager currentTable = dbManager.getCurrentTable();
        List<List<Map<String, String>>> currentTableHashMap = currentTable.getTableHashMap();
        String key = args[0];
        int dirNum = currentTable.keyValueDirNum(key);
        int fileNum = currentTable.keyValueFileNum(key);
        Map<String, String> mapWithKey = currentTableHashMap.get(dirNum).get(fileNum);
        String value = mapWithKey.get(key);
        if (value == null) {
            msg = new String("not found");
        } else {
            msg = new String("found" + System.lineSeparator() + value);
        }
    }
}
