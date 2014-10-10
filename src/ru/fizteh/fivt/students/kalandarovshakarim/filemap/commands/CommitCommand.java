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
public class CommitCommand extends AbstractCommand<OneTableBase> {

    public CommitCommand(OneTableBase context) {
        super("commit", 0, context);
    }

    @Override
    public void exec(String[] args) throws IOException {
        Table activeTable = context.getActiveTable();

        if (activeTable == null) {
            throw new IOException("no table");
        }

        int changes = activeTable.commit();

        System.out.println(changes);
    }

}
