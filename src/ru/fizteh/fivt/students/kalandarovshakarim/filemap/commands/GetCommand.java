/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.filemap.commands;

import java.io.IOException;
import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.students.kalandarovshakarim.filemap.table.OneTableBase;

/**
 *
 * @author shakarim
 */
public class GetCommand extends AbstractTableCommand {

    public GetCommand(OneTableBase context) {
        super("get", 1, context);
    }

    @Override
    protected void onActiveTable(Table activeTable, String[] args) throws IOException {
        String value = activeTable.get(args[0]);

        if (value == null) {
            throw new IOException("not found");
        } else {
            System.out.println(value);
            System.out.println("found");
        }
    }
}
