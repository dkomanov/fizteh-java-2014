/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.commands.table;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.kalandarovshakarim.database.OneTableBase;

/**
 *
 * @author shakarim
 */
public class PutCommand extends AbstractTableCommand {

    public PutCommand(OneTableBase context) {
        super("put", 2, context);
    }

    @Override
    protected void onActiveTable(Table activeTable, String[] args) {
        String oldValue = activeTable.put(args[0], args[1]);

        if (oldValue == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(oldValue);
        }
    }
}
