package ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.filemap.Cmd;

import ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.MultiFileHashMapManager;

public final class RemoveCommand extends FileMapCommand {

    public void execute(final String[] args, MultiFileHashMapManager parser) {
        if (checkWorkTable(parser.getWorkTable())) {
            checkArgs(2, args);
            if (parser.getWorkTable().getBase().get(args[1]) != null) {
                parser.getWorkTable().removeBase(args[1]);
                System.out.println("removed");
            } else {
                System.out.println("not found");
            }
        }
    }
}
