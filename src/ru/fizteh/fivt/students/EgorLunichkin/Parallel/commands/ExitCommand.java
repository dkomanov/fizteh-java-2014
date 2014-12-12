package ru.fizteh.fivt.students.EgorLunichkin.Parallel.commands;

import ru.fizteh.fivt.students.EgorLunichkin.Parallel.*;

public class ExitCommand implements Command {
    public ExitCommand() {}

    @Override
    public void run() throws ExitException {
        throw new ExitException(0);
    }

    @Override
    public void putArguments(ParallelTableProvider ptp, String[] args) throws ParallelException {
        if (args.length > maxArguments()) {
            throw new ParallelException("exit: Too mant arguments");
        }
    }

    @Override
    public int minArguments() {
        return 0;
    }

    @Override
    public int maxArguments() {
        return 0;
    }
}
