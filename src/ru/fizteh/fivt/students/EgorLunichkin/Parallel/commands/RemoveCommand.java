package ru.fizteh.fivt.students.EgorLunichkin.Parallel.commands;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.EgorLunichkin.Parallel.*;

public class RemoveCommand implements Command {
    public RemoveCommand() {}

    private ParallelTableProvider base;
    private String key;

    @Override
    public void run() {
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            Storeable value = base.getUsing().remove(key);
            if (value == null) {
                System.out.println("not found");
            } else {
                System.out.println("removed");
            }
        }
    }

    @Override
    public void putArguments(ParallelTableProvider ptp, String[] args) throws ParallelException {
        if (args.length > maxArguments()) {
            throw new ParallelException("remove: Too many arguments");
        }
        if (args.length < minArguments()) {
            throw new ParallelException("remove: Too few arguments");
        }
        key = args[0];
        base = ptp;
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
