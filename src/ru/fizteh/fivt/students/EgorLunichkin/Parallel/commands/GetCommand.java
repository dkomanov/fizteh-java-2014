package ru.fizteh.fivt.students.EgorLunichkin.Parallel.commands;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.EgorLunichkin.Parallel.*;

public class GetCommand implements Command {
    public GetCommand(ParallelTableProvider ptp, String givenKey) {
        key = givenKey;
        base = ptp;
    }

    private ParallelTableProvider base;
    private String key;

    @Override
    public void run() {
        if (base.getUsing() == null) {
            System.out.println("no table");
        } else {
            Storeable value = base.getUsing().get(key);
            if (value == null) {
                System.out.println("not found");
            } else {
                System.out.println("found\n" + sTableProvider.serialize(sTableProvider.getUsing(), value));
            }
        }
    }
}
