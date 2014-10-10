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
public class GetCommand extends AbstractCommand<OneTableBase> {

    public GetCommand(OneTableBase context) {
        super("get", 1, context);
    }

    @Override
    public void exec(String[] args) throws IOException {
        Table activeTable = context.getActiveTable();

        if (activeTable == null) {
            throw new IOException("no table");
        }

        String value = activeTable.get(args[0]);

        if (value == null) {
            throw new IOException("not found");
        } else {
            System.out.println(value);
            System.out.println("found");
        }
    }
}
