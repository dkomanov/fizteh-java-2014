package ru.fizteh.fivt.students.EgorLunichkin.Parallel.commands;

import ru.fizteh.fivt.students.EgorLunichkin.Parallel.*;

public class UseCommand implements Command {
    public UseCommand() {}

    private ParallelTableProvider base;
    private String tableName;

    @Override
    public void run() {
        if (base.getUsing() != null && base.getUsing().getNumberOfUncommittedChanges() > 0) {
            System.out.println(base.getUsing().getNumberOfUncommittedChanges() + " unsaved changes");
        } else if (base.setUsing(tableName) == null) {
            System.out.println(tableName + " not exists");
        } else {
            System.out.println("using " + tableName);
        }
    }

    @Override
    public void putArguments(ParallelTableProvider ptp, String[] args) throws ParallelException {
        if (args.length > maxArguments()) {
            throw new ParallelException("use: Too many arguments");
        }
        if (args.length < minArguments()) {
            throw new ParallelException("use: Too few arguments");
        }
        tableName = args[0];
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
