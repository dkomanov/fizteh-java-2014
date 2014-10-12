/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.multifilehashmap.commands;

import ru.fizteh.fivt.students.kalandarovshakarim.multifilehashmap.DataBase;
import ru.fizteh.fivt.students.kalandarovshakarim.shell.commands.AbstractCommand;

/**
 *
 * @author shakarim
 */
public class CreateCommand extends AbstractCommand<DataBase> {

    public CreateCommand(DataBase context) {
        super("create", 1, context);
    }

    @Override
    public void exec(String[] args) {
        context.getProvider().createTable(args[0]);
        System.out.println("created");
    }
}
