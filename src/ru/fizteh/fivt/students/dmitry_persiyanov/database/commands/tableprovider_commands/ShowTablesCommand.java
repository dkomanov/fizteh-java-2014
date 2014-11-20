package ru.fizteh.fivt.students.dmitry_persiyanov.database.commands.tableprovider_commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.database.commands.DbCommand;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table_provider.DbTableProvider;

import java.io.PrintStream;
import java.util.List;


public class ShowTablesCommand extends DbCommand {
    public ShowTablesCommand(final String[] args, final DbTableProvider tableProvider) {
        super("show tables", 0, args, tableProvider);
    }

    @Override
    protected void execute(final PrintStream out) {
        List<String> tableNames = tableProvider.getTableNames();
        if (tableNames.size() != 0) {
            StringBuilder msgBuilder = new StringBuilder();
            for (String tableName : tableNames) {
                int tableSize = tableProvider.getTable(tableName).size();
                msgBuilder.append(tableName + " " + tableSize);
                msgBuilder.append(System.lineSeparator());
            }
            msgBuilder.delete(msgBuilder.length() - 1, msgBuilder.length());
            out.println(msgBuilder.toString());
        }
    }
}
