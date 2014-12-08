package ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands.db_manager_commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.DbManager;

import java.io.IOException;
import java.util.Map;


public class ShowTablesCommand extends DbManagerCommand {
    public ShowTablesCommand(final String[] args) {
        super("show tables", args);
    }

    @Override
    public void execute(final DbManager dbManager) throws IOException {
        StringBuilder msgBuilder = new StringBuilder();
        Map<String, Integer> tableNames = dbManager.showTables();
        if (tableNames.size() == 0) {
            msg = "";
        } else {
            for (Map.Entry<String, Integer> entry : tableNames.entrySet()) {
                msgBuilder.append(entry.getKey() + " " + entry.getValue());
                msgBuilder.append(System.lineSeparator());
            }
            msgBuilder.delete(msgBuilder.length() - 1, msgBuilder.length());
            msg = msgBuilder.toString();
        }
    }
}
