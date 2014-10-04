/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.filemap.commands;

import java.io.FileNotFoundException;
import java.io.IOException;
import ru.fizteh.fivt.students.kalandarovshakarim.filemap.FileMapShellState;
import ru.fizteh.fivt.students.kalandarovshakarim.shell.commands.AbstractCommand;

/**
 *
 * @author shakarim
 */
public class ExitCommand extends AbstractCommand<FileMapShellState> {

    public ExitCommand() {
        super("exit", 0);
    }

    @Override
    public void exec(FileMapShellState state, String args)
            throws FileNotFoundException, IOException {
        state.getState().save();
        System.exit(0);
    }

}
