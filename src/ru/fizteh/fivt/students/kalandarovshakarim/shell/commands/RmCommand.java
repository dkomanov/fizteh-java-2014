/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.shell.commands;

import java.io.FileNotFoundException;
import java.io.IOException;
import ru.fizteh.fivt.students.kalandarovshakarim.shell.ShellState;

/**
 *
 * @author Shakarim
 */
public class RmCommand extends AbstractCommand<ShellState> {

    public RmCommand() {
        super("rm", 1);
    }

    @Override
    public void exec(ShellState state, String args)
            throws FileNotFoundException, IOException {
        String[] params = CommandParser.getParams(args);
        boolean rec = CommandParser.isRec(args);
        int opt = (rec ? 1 : 0);
        if (this.getArgsNum() + opt != params.length) {
            throw new IOException("invalid number of arguments");
        }

        state.getState().rm(params[0], rec);
    }
}
