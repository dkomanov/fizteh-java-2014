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
public class GetCommand extends AbstractCommand<FileMapShellState> {

    public GetCommand() {
        super("get", 1);
    }

    @Override
    public void exec(FileMapShellState state, String args)
            throws IOException {
        String[] params = CommandParser.getParams(args);

        if (this.getArgsNum() != params.length) {
            throw new IOException("invalid number of arguments");
        }

        String value = state.getState().get(params[0]);
        if (value == null) {
            System.out.println("not found");
        } else {
            System.out.println(value);
            System.out.println("found");
        }
    }

}
