package ru.fizteh.fivt.students.kinanAlsarmini.filemap.base.commands;

import ru.fizteh.fivt.students.kinanAlsarmini.shell.commands.AbstractCommand;
import ru.fizteh.fivt.students.kinanAlsarmini.shell.commands.CommandParser;

public class ExitCommand<State extends BaseFileMapShellState> extends AbstractCommand<State> {
    public ExitCommand() {
        super("exit", "exit");
    }

    public void executeCommand(String params, State state) {
        if (CommandParser.getParametersCount(params) > 0) {
            throw new IllegalArgumentException("too many arguments");
        }

        if (state.getTable() != null) {
            state.rollback();
        }

        System.exit(0);
    }
}
