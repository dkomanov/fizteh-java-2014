/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.commands.database;

import java.util.List;
import ru.fizteh.fivt.students.kalandarovshakarim.database.DataBase;
import ru.fizteh.fivt.students.kalandarovshakarim.database.DataBaseProvider;
import ru.fizteh.fivt.students.kalandarovshakarim.commands.AbstractCommand;

/**
 *
 * @author shakarim
 */
public class ShowTablesCommand extends AbstractCommand<DataBase> {

    public ShowTablesCommand(DataBase context) {
        super("show", 1, context);
    }

    @Override
    public void exec(String[] args) {

        if (!"tables".equals(args[0])) {
            throw new IllegalArgumentException(args[0] + " Unknown argument");
        }

        DataBaseProvider provider = (DataBaseProvider) context.getProvider();
        List<String> list = provider.listTables();
        System.out.printf("%-25srow_num%n", "table_name");
        String format = "%-25s%d%n";
        for (String tableName : list) {
            if (provider.getTable(tableName) != null) {
                int rowNumber = provider.getTable(tableName).size();
                System.out.printf(format, tableName, rowNumber);
            } else {
                System.out.printf("%-25s%d%n", tableName, "corrupted or access denied");
            }
        }
    }
}
