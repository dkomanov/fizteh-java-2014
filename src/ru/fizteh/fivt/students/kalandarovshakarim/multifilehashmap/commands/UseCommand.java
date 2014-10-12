/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.multifilehashmap.commands;

import java.io.IOException;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.kalandarovshakarim.filemap.table.AbstractTable;
import ru.fizteh.fivt.students.kalandarovshakarim.multifilehashmap.DataBase;
import ru.fizteh.fivt.students.kalandarovshakarim.shell.commands.AbstractCommand;

/**
 *
 * @author shakarim
 */
public class UseCommand extends AbstractCommand<DataBase> {

    public UseCommand(DataBase context) {
        super("use", 1, context);
    }

    @Override
    public void exec(String[] args) throws IOException {
        Table newTable = context.getProvider().getTable(args[0]);
        Table currentTable = context.getActiveTable();
        int changes = 0;

        if (currentTable != null) {
            changes = ((AbstractTable) currentTable).changes();
        }

        if (changes == 0) {
            context.setActiveTable(newTable);
            System.out.printf("using %s", args[0]);
            System.out.println();
        } else {
            String msg = "%d unsaved changes";
            throw new IOException(String.format(msg, changes));
        }
    }
}
