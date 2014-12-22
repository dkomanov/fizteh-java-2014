package ru.fizteh.fivt.students.EgorLunichkin.Parallel.commands;

import ru.fizteh.fivt.students.EgorLunichkin.Parallel.*;

public class ShowTablesCommand implements Command {
    public ShowTablesCommand() {}

    private ParallelTableProvider base;

    @Override
    public void run() {
        for (String tableName : base.getTableNames()) {
            System.out.println(tableName + " " + base.getTable(tableName).size());
        }
    }

    @Override
    public void putArguments(ParallelTableProvider ptp, String[] args) throws ParallelException {
        if (args.length < minArguments() || !args[0].equals("tables")) {
            throw new ParallelException("Unknown command");
        }
        if (args.length > maxArguments()) {
            throw new ParallelException("show tables: Too many arguments");
        }
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
