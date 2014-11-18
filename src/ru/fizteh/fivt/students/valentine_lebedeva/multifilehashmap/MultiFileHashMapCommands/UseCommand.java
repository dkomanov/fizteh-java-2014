package ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.MultiFileHashMapCommands;

import java.io.IOException;

import ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.MultiFileHashMapManager;

public final class UseCommand extends MultiFileHashMapCommand {
    @Override
    public void execute(final String[] args, MultiFileHashMapManager manager) throws IOException {
        checkArgs(2, args);
        if (manager.getTables().containsKey(args[1])) {
            manager.setWorkTable(args[1]);
            System.out.println("using " + args[1]);
        } else {
            System.out.println(args[1] + " not exists");
        }
    }
}
