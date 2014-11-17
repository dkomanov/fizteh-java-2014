/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.multifilehashmap.commands;

import ru.fizteh.fivt.students.kalandarovshakarim.multifilehashmap.database.DataBase;
import ru.fizteh.fivt.students.kalandarovshakarim.shell.commands.AbstractCommand;

/**
 *
 * @author shakarim
 */
public class DropCommand extends AbstractCommand<DataBase> {

    public DropCommand(DataBase context) {
        super("drop", 1, context);
    }

    @Override
    public void exec(String[] args) {
        context.getProvider().removeTable(args[0]);
        if (args[0].equals(context.getActiveTable().getName())) {
            context.setActiveTable(null);
        }
        System.out.println("dropped");
    }
}
