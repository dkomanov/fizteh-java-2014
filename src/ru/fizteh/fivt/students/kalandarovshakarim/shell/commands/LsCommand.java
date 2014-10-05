/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.shell.commands;

import java.io.IOException;
import ru.fizteh.fivt.students.kalandarovshakarim.shell.ShellState;

/**
 *
 * @author Shakarim
 */
public class LsCommand extends AbstractCommand<ShellState> {

    public LsCommand() {
        super("ls", 0);
    }

    @Override
    public void exec(ShellState state, String args) throws IOException {
        String[] params = CommandParser.getParams(args);

        if (this.getArgsNum() != params.length) {
            throw new IOException("invalid number of arguments");
        }

        String[] names = state.getState().listFiles();
        for (String s : names) {
            if (s.charAt(0) != '.') {
                System.out.println(s);
            }
        }
    }
}
