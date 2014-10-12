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
public class PutCommand extends AbstractCommand<FileMapShellState> {

    public PutCommand() {
        super("put", 2);
    }

    @Override
    public void exec(FileMapShellState state, String args)
            throws IOException {
        String[] params = CommandParser.getParams(args);

        if (this.getArgsNum() != params.length) {
            throw new IOException("invalid number of arguments");
        }

        String oldValue = state.getState().put(params[0], params[1]);

        if (oldValue == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
            System.out.println(oldValue);
        }
    }

}
