package ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.DbManager;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.TableManager;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.exceptions.TableIsNotChosenException;

import java.io.IOException;
import java.util.*;

public class ListCommand extends DbCommand {
    public ListCommand(final String[] args) {
        super("list", args);
        NUM_OF_ARGS = 0;
        checkArgs();
    }

    @Override
    public void execute(final DbManager dbManager) throws IOException, TableIsNotChosenException {
        if (dbManager.getCurrentTable() == null) {
            throw new TableIsNotChosenException();
        }
        List<List<Map<String, String>>> currentTableHashMap = dbManager.getCurrentTable().getTableHashMap();
        if (currentTableHashMap != null) {
            List<String> allKeys = new LinkedList<>();
            for (List<Map<String, String>> list : currentTableHashMap) {
                for (Map<String, String> map : list) {
                    Set<String> keySet = map.keySet();
                    Iterator<String> keySetIter = keySet.iterator();
                    List<String> keysList = new LinkedList<>();
                    while (keySetIter.hasNext()) {
                        keysList.add(keySetIter.next());
                    }
                    if (keysList.size() != 0) {
                        allKeys.add(String.join(", ", keysList));
                    }
                }
            }
            if (allKeys.size() != 0) {
                msg = String.join(", ", allKeys);
            } else {
                msg = new String("");
            }
        }
    }
}
