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
public class ListCommand extends AbstractCommand<OneTableBase> {

    public ListCommand(OneTableBase context) {
        super("list", 0, context);
    }

    @Override
    public void exec(String[] args) {
        Table activeTable = context.getActiveTable();

        if (activeTable == null) {
            throw new IllegalArgumentException("no table");
        }
        StringBuilder keyList = new StringBuilder();
        for (String key : activeTable.list()) {
            keyList.append(key);
            keyList.append(", ");
        }
        if (keyList.lastIndexOf(", ") != -1) {
            keyList.setLength(keyList.length() - 2);
        }
        System.out.println(keyList);
    }
}
