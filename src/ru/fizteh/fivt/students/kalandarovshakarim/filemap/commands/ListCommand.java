/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.fizteh.fivt.students.kalandarovshakarim.filemap.commands;

import java.io.IOException;
import ru.fizteh.fivt.students.kalandarovshakarim.filemap.FileMapShellState;
import ru.fizteh.fivt.students.kalandarovshakarim.shell.commands.AbstractCommand;
import ru.fizteh.fivt.students.kalandarovshakarim.shell.commands.CommandParser;

/**
 *
 * @author shakarim
 */
public class ListCommand extends AbstractCommand<FileMapShellState> {

    public ListCommand() {
        super("list", 0);
    }

    @Override
    public void exec(FileMapShellState state, String args) throws IOException {
        String[] params = CommandParser.getParams(args);

        if (this.getArgsNum() != params.length) {
            throw new IOException("invalid number of arguments");
        }

        String[] keys = state.getState().list();

        for (String key : keys) {
            System.out.println(key);
        }
    }
}
