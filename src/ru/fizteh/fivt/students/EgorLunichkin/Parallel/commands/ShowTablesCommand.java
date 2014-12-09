package ru.fizteh.fivt.students.EgorLunichkin.Parallel.commands;

import ru.fizteh.fivt.students.EgorLunichkin.Parallel.*;

public class ShowTablesCommand implements Command {
    public ShowTablesCommand(ParallelTableProvider ptp) {
        base = ptp;
    }

    private ParallelTableProvider base;

    @Override
    public void run() {
        for (String tableName : base.getTableNames()) {
            System.out.println(tableName + " " + base.getTable(tableName).size());
        }
    }
}
