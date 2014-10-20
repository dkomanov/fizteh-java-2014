package ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.DbManager;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.TableManager;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.exceptions.TableIsNotChosenException;

import java.io.IOException;
import java.util.Map;

public class RemoveCommand extends DbCommand {
    public RemoveCommand(final String[] args) {
        super("remove", args);
        NUM_OF_ARGS = 1;
        checkArgs();
    }

    @Override
    public void execute(final DbManager dbManager) throws IOException, TableIsNotChosenException {
        if (dbManager.getCurrentTable() == null) {
            throw new TableIsNotChosenException();
        }
        String key = args[0];
        Map<String, String> keyMap = dbManager.getCurrentTable().getTableHashMap()
                .get(TableManager.keyValueDirNum(key)).get(TableManager.keyValueFileNum(key));
        String value = keyMap.remove(key);
        if (value == null) {
            msg = new String("not found");
        } else {
            msg = new String("removed");
        }
    }
}
