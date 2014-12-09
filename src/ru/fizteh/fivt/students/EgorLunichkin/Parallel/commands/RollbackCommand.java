package ru.fizteh.fivt.students.EgorLunichkin.Parallel.commands;

import ru.fizteh.fivt.students.EgorLunichkin.Parallel.*;

public class RollbackCommand implements Command {
    public RollbackCommand(ParallelTableProvider ptp) {
        base = ptp;
    }

    private ParallelTableProvider base;

    @Override
    public void run() {
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            System.out.println(base.getUsing().rollback());
        }
    }
}
