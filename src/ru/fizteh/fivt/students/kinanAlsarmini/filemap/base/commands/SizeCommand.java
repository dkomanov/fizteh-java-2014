package ru.fizteh.fivt.students.kinanAlsarmini.filemap.base.commands;

import ru.fizteh.fivt.students.kinanAlsarmini.shell.commands.AbstractCommand;
import ru.fizteh.fivt.students.kinanAlsarmini.shell.commands.CommandParser;

public class SizeCommand<State extends BaseFileMapShellState> extends AbstractCommand<State> {
    public SizeCommand() {
        super("size", "size");
    }

    public void executeCommand(String params, State shellState) {
        if (CommandParser.getParametersCount(params) > 0) {
            throw new IllegalArgumentException("too many arguments");
        }

        if (shellState.getTable() == null) {
            System.err.println("no table");
            return;
        }

        int size = shellState.size();
        System.out.println(size);
    }
}
