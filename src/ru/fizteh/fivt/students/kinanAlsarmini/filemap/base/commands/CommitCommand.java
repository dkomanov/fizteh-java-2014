package ru.fizteh.fivt.students.kinanAlsarmini.filemap.base.commands;

import ru.fizteh.fivt.students.kinanAlsarmini.shell.commands.AbstractCommand;
import ru.fizteh.fivt.students.kinanAlsarmini.shell.commands.CommandParser;

public class CommitCommand<State extends BaseFileMapShellState> extends AbstractCommand<State> {
    public CommitCommand() {
        super("commit", "commit");
    }

    public void executeCommand(String params, BaseFileMapShellState state) {
        if (CommandParser.getParametersCount(params) > 0) {
            throw new IllegalArgumentException("too many arguments");
        }

        if (state.getTable() == null) {
            System.err.println("no table");
            return;
        }

        System.out.println(state.commit());
    }
}
