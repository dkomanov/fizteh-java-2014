package ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.filemap.Cmd;

import ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.MultiFileHashMapManager;

public final class RemoveCommand extends FileMapCommand {
    @Override
    public void execute(final String[] args, MultiFileHashMapManager manager) {
        if (checkWorkTable(manager.getWorkTable())) {
            checkArgs(2, args);
            if (manager.getWorkTable().getBase().containsKey(args[1])) {
                manager.getWorkTable().removeBase(args[1]);
                System.out.println("removed");
            } else {
                System.out.println("not found");
            }
        }
    }
}
