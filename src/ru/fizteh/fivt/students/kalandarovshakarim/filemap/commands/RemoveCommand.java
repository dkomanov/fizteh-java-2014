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
public class RemoveCommand extends AbstractCommand<OneTableBase> {

    public RemoveCommand(OneTableBase context) {
        super("remove", 1, context);
    }

    @Override
    public void exec(String[] args) throws IOException {
        Table activeTable = context.getActiveTable();

        if (activeTable == null) {
            throw new IllegalArgumentException("no table");
        }
        String deleted = activeTable.remove(args[0]);

        if (deleted == null) {
            throw new IOException("not found");
        } else {
            System.out.println("removed");
        }
    }
}
