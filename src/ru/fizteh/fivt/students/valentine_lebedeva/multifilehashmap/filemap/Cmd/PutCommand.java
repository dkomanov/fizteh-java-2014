package ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.filemap.Cmd;

import ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.MultiFileHashMapManager;

public final class PutCommand extends FileMapCommand {
    @Override
    public void execute(final String[] args, MultiFileHashMapManager manager) {
        if (checkWorkTable(manager.getWorkTable())) {
            checkArgs(3, args);
            if (manager.getWorkTable().getBase().containsKey(args[1])) {
                System.out.println("overwrite");
                System.out.println(args[2]);
            } else {
                System.out.println("new");
            }
            manager.getWorkTable().putBase(args[1], args[2]);
        }
    }
}
