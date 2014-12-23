package ru.fizteh.fivt.students.EgorLunichkin.Storeable.commands;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.EgorLunichkin.Storeable.StoreableTableProvider;

public class RemoveCommand implements Command {
    public RemoveCommand(StoreableTableProvider stp, String givenKey) {
        key = givenKey;
        sTableProvider = stp;
    }

    private StoreableTableProvider sTableProvider;
    private String key;

    @Override
    public void run() {
        if (sTableProvider.getUsing() == null) {
            System.out.println("no table");
        } else {
            Storeable value = sTableProvider.getUsing().remove(key);
            if (value == null) {
                System.out.println("not found");
            } else {
                System.out.println("removed");
            }
        }
    }
}
