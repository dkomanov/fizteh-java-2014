/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.shell.commands;

import ru.fizteh.fivt.students.kalandarovshakarim.shell.ShellState;

/**
 *
 * @author Shakarim
 */
public class ExitCommand extends AbstractCommand<ShellState> {

    public ExitCommand() {
        super("exit", 0);
    }

    @Override
    public void exec(ShellState state, String args) {
        System.exit(0);
    }

}
