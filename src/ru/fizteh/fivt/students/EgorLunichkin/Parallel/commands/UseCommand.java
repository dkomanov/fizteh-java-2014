package ru.fizteh.fivt.students.EgorLunichkin.Parallel.commands;

import ru.fizteh.fivt.students.EgorLunichkin.Parallel.*;

public class UseCommand implements Command {
    public UseCommand(ParallelTableProvider ptp, String givenName) {
        tableName = givenName;
        base = ptp;
    }

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
}
