/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.shell.commands;

import ru.fizteh.fivt.students.kalandarovshakarim.shell.ShellState;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 *
 * @author Shakarim
 */
public class PwdCommand extends AbstractCommand<ShellState> {

    public PwdCommand() {
        super("pwd", 0);
    }

    @Override
    public void exec(ShellState shellState, String args)
            throws FileNotFoundException, IOException {
        String[] params = CommandParser.getParams(args);

        if (this.getArgsNum() != params.length) {
            throw new IOException("invalid number of arguments");
        }

        String path = shellState.getState().getCwd();
        System.out.println(path);
    }

}
