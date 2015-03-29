package ru.fizteh.fivt.students.hromov_igor.multifilemap.commands;

import ru.fizteh.fivt.students.hromov_igor.multifilemap.interpreter.exception.ExitCommandException;

public class Exit extends ParentCommand {

    public Exit(CommandState state) {
        super(state);
    }

    @Override
    public void run() {
        throw new ExitCommandException();
    }

    @Override
    public int requiredArgsNum() {
        return 0;
    }
}
