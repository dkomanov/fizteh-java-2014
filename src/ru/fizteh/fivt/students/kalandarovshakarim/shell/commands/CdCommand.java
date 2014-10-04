/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.shell.commands;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import ru.fizteh.fivt.students.kalandarovshakarim.shell.ShellState;

/**
 *
 * @author Shakarim
 */
public class CdCommand extends AbstractCommand<ShellState> {

    public CdCommand() {
        super("cd", 1);
    }

    @Override
    public void exec(ShellState state, String args)
            throws NoSuchFileException, FileNotFoundException, IOException {
        String[] params = CommandParser.getParams(args);

        if (this.getArgsNum() != params.length) {
            throw new IOException("invalid number of arguments");
        }

        state.getState().chDir(params[0]);
    }

}
