/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.filemap.commands;

import java.io.IOException;
import java.util.Iterator;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.kalandarovshakarim.filemap.table.OneTableBase;
import ru.fizteh.fivt.students.kalandarovshakarim.shell.commands.AbstractCommand;

/**
 *
 * @author shakarim
 */
public class ListCommand extends AbstractCommand<OneTableBase> {

    public ListCommand(OneTableBase context) {
        super("list", 0, context);
    }

    @Override
    public void exec(String[] args) throws IOException {
        Table activeTable = context.getActiveTable();

        if (activeTable == null) {
            throw new IOException("no table");
        }

        Iterator<String> iterator = activeTable.list().iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }
}
