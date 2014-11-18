package ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.MultiFileHashMapCommands;

import java.io.IOException;

import ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.MultiFileHashMapManager;

public final class DropCommand extends MultiFileHashMapCommand {
    @Override
    public void execute(final String[] args, MultiFileHashMapManager manager) throws IOException {
        checkArgs(2, args);
        if (manager.getTables().containsKey(args[1])) {
            manager.removeTable(args[1]);
            if (manager.getWorkTable().getName().equals(args[1])) {
                manager.setWorkTable(null);
            }
            System.out.println("dropped");
        } else {
            System.out.println(args[1] + " not exists");
        }
    }
}
