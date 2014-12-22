package ru.fizteh.fivt.students.EgorLunichkin.Storeable.commands;

import ru.fizteh.fivt.students.EgorLunichkin.Storeable.StoreableTableProvider;

public class SizeCommand implements Command {
    public SizeCommand(StoreableTableProvider stp) {
        sTableProvider = stp;
    }

    private StoreableTableProvider sTableProvider;

    @Override
    public void run() {
        if (sTableProvider.getUsing() == null) {
            System.out.println("no table");
        } else {
            System.out.println(sTableProvider.getUsing().size());
        }
    }
}
