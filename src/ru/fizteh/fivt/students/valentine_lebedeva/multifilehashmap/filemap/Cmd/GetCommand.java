package ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.filemap.Cmd;

import ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.MultiFileHashMapManager;

public final class GetCommand extends FileMapCommand {
    @Override
    public void execute(final String[] args, MultiFileHashMapManager parser) {
        if (checkWorkTable(parser.getWorkTable())) {
            checkArgs(2, args);
            if (parser.getWorkTable().getBase().containsKey(args[1])) {
                System.out.println("found");
                System.out
                        .println(parser.getWorkTable().getBase().get(args[1]));
            } else {
                System.out.println("not found");
            }
        }
    }
}
