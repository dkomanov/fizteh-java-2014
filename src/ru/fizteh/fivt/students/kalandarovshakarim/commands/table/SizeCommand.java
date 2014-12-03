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
public class SizeCommand extends AbstractTableCommand {

    public SizeCommand(OneTableBase context) {
        super("size", 0, context);
    }

    @Override
    protected void onActiveTable(Table activeTable, String[] args) {
        int size = activeTable.size();
        System.out.println(size);
    }
}
