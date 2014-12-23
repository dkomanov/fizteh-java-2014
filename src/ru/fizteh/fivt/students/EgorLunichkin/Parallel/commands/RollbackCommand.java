package ru.fizteh.fivt.students.EgorLunichkin.Parallel.commands;

import ru.fizteh.fivt.students.EgorLunichkin.Parallel.*;

public class RollbackCommand implements Command {
    public RollbackCommand() {}

    private ParallelTableProvider base;

    @Override
    public void run() {
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            System.out.println(base.getUsing().rollback());
        }
    }

    @Override
    public void putArguments(ParallelTableProvider ptp, String[] args) throws ParallelException {
        if (args.length > maxArguments()) {
            throw new ParallelException("rollback: Too many arguments");
        }
        base = ptp;
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
