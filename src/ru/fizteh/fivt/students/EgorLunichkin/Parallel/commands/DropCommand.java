package ru.fizteh.fivt.students.EgorLunichkin.Parallel.commands;

import ru.fizteh.fivt.students.EgorLunichkin.Parallel.*;

import java.io.IOException;

public class DropCommand implements Command {
    public DropCommand() {}

    private ParallelTableProvider base;
    private String tableName;

    @Override
    public void run() throws IOException {
        try {
            base.removeTable(tableName);
            System.out.println("dropped");
        } catch (IllegalStateException ex) {
            System.out.println(tableName + " not found");
        }
    }

    @Override
    public void putArguments(ParallelTableProvider ptp, String[] args) throws ParallelException {
        if (args.length > maxArguments()) {
            throw new ParallelException("drop: Too many arguments");
        }
        if (args.length < minArguments()) {
            throw new ParallelException("drop: Too many arguments");
        }
        base = ptp;
        tableName = args[0];
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
