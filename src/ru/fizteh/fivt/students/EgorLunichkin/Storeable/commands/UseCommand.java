package ru.fizteh.fivt.students.EgorLunichkin.Storeable.commands;

import ru.fizteh.fivt.students.EgorLunichkin.Storeable.StoreableTableProvider;

public class UseCommand implements Command {
    public UseCommand(StoreableTableProvider stp, String givenName) {
        tableName = givenName;
        sTableProvider = stp;
    }

    private StoreableTableProvider sTableProvider;
    private String tableName;

    @Override
    public void run() {
        if (sTableProvider.getUsing() != null && sTableProvider.getUsing().getNumberOfUncommittedChanges() > 0) {
            System.out.println(sTableProvider.getUsing().getNumberOfUncommittedChanges() + " unsaved changes");
        } else if (sTableProvider.setUsing(tableName) == null) {
            System.out.println(tableName + " not exists");
        } else {
            System.out.println("using " + tableName);
        }
    }
}
