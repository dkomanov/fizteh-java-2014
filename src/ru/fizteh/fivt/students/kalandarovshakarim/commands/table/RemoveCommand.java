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
public class RemoveCommand extends AbstractTableCommand {

    public RemoveCommand(OneTableBase context) {
        super("remove", 1, context);
    }

    @Override
    protected void onActiveTable(Table activeTable, String[] args) {
        String deleted = activeTable.remove(args[0]);

        if (deleted == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }
}
