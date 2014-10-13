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
public class SizeCommand extends AbstractCommand<OneTableBase> {

    public SizeCommand(OneTableBase context) {
        super("size", 0, context);
    }

    @Override
    public void exec(String[] args) {
        Table activeTable = context.getActiveTable();

        if (activeTable == null) {
            throw new IllegalArgumentException("no table");
        }

        int size = activeTable.size();
        System.out.println(size);
    }
}
