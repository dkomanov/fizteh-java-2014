/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.filemap.commands;

import java.io.IOException;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.kalandarovshakarim.filemap.table.OneTableBase;
import ru.fizteh.fivt.students.kalandarovshakarim.shell.commands.AbstractCommand;

/**
 *
 * @author shakarim
 */
public abstract class AbstractTableCommand extends AbstractCommand<OneTableBase> {

    public AbstractTableCommand(String name, int argNum, OneTableBase context) {
        super(name, argNum, context);
    }

    @Override
    public void exec(String[] args) throws IOException {
        Table activeTable = context.getActiveTable();
        if (activeTable == null) {
            throw new IllegalArgumentException("no table");
        } else {
            onActiveTable(activeTable, args);
        }
    }

    protected abstract void onActiveTable(Table activeTable, String[] args) throws IOException;
}
