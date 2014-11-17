package ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table_provider.commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table_provider.DbTableProvider;

import java.io.PrintStream;
import java.util.Map;


public class ShowTablesCommand extends DbTableProviderCommand {
    public ShowTablesCommand(final String[] args, final DbTableProvider relatedDb) {
        super("show tables", 0, args, relatedDb);
    }

    @Override
    protected void execute(final PrintStream out) {
        StringBuilder msgBuilder = new StringBuilder();
        Map<String, Integer> tableNames = relatedDb.showTables();
        if (tableNames.size() != 0) {
            for (Map.Entry<String, Integer> entry : tableNames.entrySet()) {
                msgBuilder.append(entry.getKey() + " " + entry.getValue());
                msgBuilder.append(System.lineSeparator());
            }
            msgBuilder.delete(msgBuilder.length() - 1, msgBuilder.length());
            out.println(msgBuilder.toString());
        }
    }
}
