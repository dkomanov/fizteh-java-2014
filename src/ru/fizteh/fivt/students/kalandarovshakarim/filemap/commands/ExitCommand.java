/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.filemap.commands;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.kalandarovshakarim.filemap.table.OneTableBase;
import ru.fizteh.fivt.students.kalandarovshakarim.shell.commands.AbstractExit;

/**
 *
 * @author shakarim
 */
public class ExitCommand extends AbstractExit<OneTableBase> {

    public ExitCommand(OneTableBase context) {
        super(context);
    }

    @Override
    protected void onExit() {
        Table activeTable = context.getActiveTable();

        if (activeTable != null) {
            activeTable.commit();
        }
    }
}
