package ru.fizteh.fivt.students.EgorLunichkin.Parallel.commands;

import ru.fizteh.fivt.students.EgorLunichkin.Parallel.*;

import java.io.IOException;

public class CommitCommand implements Command {
    public CommitCommand() {}

    private ParallelTableProvider base;

    @Override
    public void run() throws IOException {
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            System.out.println(base.getUsing().commit());
        }
    }

    @Override
    public void putArguments(ParallelTableProvider ptp, String[] args) throws ParallelException {
        if (args.length > maxArguments()) {
            throw new ParallelException("commit: Too many arguments");
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
