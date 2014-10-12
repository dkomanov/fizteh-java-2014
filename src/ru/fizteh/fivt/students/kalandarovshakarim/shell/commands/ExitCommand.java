/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.shell.commands;

import ru.fizteh.fivt.students.kalandarovshakarim.shell.ShellUtils;

/**
 *
 * @author Shakarim
 */
public class ExitCommand extends ExitStrategy<ShellUtils> {

    public ExitCommand(ShellUtils context) {
        super(context);
    }

    @Override
    protected void onExit() {
        // Nothing to do before exit.
    }
}
