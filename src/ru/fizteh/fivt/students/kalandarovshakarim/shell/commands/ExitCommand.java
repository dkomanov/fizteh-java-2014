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
public class ExitCommand extends AbstractCommand<ShellUtils> {

    public ExitCommand(ShellUtils context) {
        super("exit", 0, context);
    }

    @Override
    public void exec(String[] args) {
        System.exit(0);
    }
}
