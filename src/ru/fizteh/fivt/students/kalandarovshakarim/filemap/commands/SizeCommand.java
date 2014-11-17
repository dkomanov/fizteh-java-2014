/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.filemap.commands;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.kalandarovshakarim.filemap.table.OneTableBase;

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
