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
public class CommitCommand extends AbstractTableCommand {

    public CommitCommand(OneTableBase context) {
        super("commit", 0, context);
    }

    @Override
    protected void onActiveTable(Table activeTable, String[] args) {
        int changes = activeTable.commit();
        System.out.println(changes);
    }
}
