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
public class ListCommand extends AbstractTableCommand {

    public ListCommand(OneTableBase context) {
        super("list", 0, context);
    }

    @Override
    protected void onActiveTable(Table activeTable, String[] args) {
        String keyList = String.join(", ", activeTable.list());
        System.out.println(keyList);
    }
}
