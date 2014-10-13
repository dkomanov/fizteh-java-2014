/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.filemap.commands;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.kalandarovshakarim.filemap.table.OneTableBase;
import ru.fizteh.fivt.students.kalandarovshakarim.shell.commands.AbstractCommand;

/**
 *
 * @author shakarim
 */
public class RollbackCommand extends AbstractCommand<OneTableBase> {

    public RollbackCommand(OneTableBase context) {
        super("rollback", 0, context);
    }

    @Override
    public void exec(String[] args) {
        Table activeTable = context.getActiveTable();

        if (activeTable == null) {
            throw new IllegalArgumentException("no table");
        }

        int changes = activeTable.rollback();
        System.out.println(changes);
    }
}
