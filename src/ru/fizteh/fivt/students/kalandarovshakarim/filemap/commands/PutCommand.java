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
public class PutCommand extends AbstractCommand<OneTableBase> {

    public PutCommand(OneTableBase context) {
        super("put", 2, context);
    }

    @Override
    public void exec(String[] args) throws IOException {
        Table activeTable = context.getActiveTable();

        if (activeTable == null) {
            throw new IOException("no table");
        }

        String oldValue = activeTable.put(args[0], args[1]);

        if (oldValue == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(oldValue);
        }
    }
}
