package ru.fizteh.fivt.students.EgorLunichkin.Storeable.commands;

import ru.fizteh.fivt.students.EgorLunichkin.Storeable.StoreableException;
import ru.fizteh.fivt.students.EgorLunichkin.Storeable.StoreableTableProvider;

import java.io.IOException;

public class CommitCommand implements Command {
    public CommitCommand(StoreableTableProvider stp) {
        sTableProvider = stp;
    }

    private StoreableTableProvider sTableProvider;

    @Override
    public void run() throws StoreableException {
        if (sTableProvider.getUsing() == null) {
            System.out.println("no table");
        } else {
            try {
                System.out.println(sTableProvider.getUsing().commit());
            } catch (IOException ex) {
                throw new StoreableException(ex.getMessage());
            }
        }
    }
}
