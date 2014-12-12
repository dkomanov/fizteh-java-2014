package ru.fizteh.fivt.students.EgorLunichkin.Parallel.commands;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.EgorLunichkin.Parallel.*;

public class GetCommand implements Command {
    public GetCommand() {}

    private ParallelTableProvider base;
    private String key;

    @Override
    public void run() {
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            Storeable value = base.getUsing().get(key);
            if (value == null) {
                System.out.println("not found");
            } else {
                System.out.println("found\n" + base.serialize(base.getUsing(), value));
            }
        }
    }

    @Override
    public void putArguments(ParallelTableProvider ptp, String[] args) throws ParallelException {
        if (args.length > maxArguments()) {
            throw new ParallelException("get: Too many arguments");
        }
        if (args.length < minArguments()) {
            throw new ParallelException("get: Too few arguments");
        }
        base = ptp;
        key = args[0];
    }

    @Override
    public int minArguments() {
        return 1;
    }

    @Override
    public int maxArguments() {
        return 1;
    }
}
