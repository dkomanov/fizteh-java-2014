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
public class ListCommand extends AbstractTableCommand {

    public ListCommand(OneTableBase context) {
        super("list", 0, context);
    }

    @Override
    protected void onActiveTable(Table activeTable, String[] args) {
        StringBuilder keyList = new StringBuilder();
        for (String key : activeTable.list()) {
            keyList.append(key);
            keyList.append(", ");
        }
        if (keyList.lastIndexOf(", ") != -1) {
            keyList.setLength(keyList.length() - 2);
        }
        System.out.println(keyList);
    }
}
