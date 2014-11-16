package ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.MultiFileHashMapCommands;

import java.io.IOException;

import ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.MultiFileHashMapManager;

public final class DropCommand extends MultiFileHashMapCommand {
    @Override
    public void execute(final String[] args, MultiFileHashMapManager parser) throws IOException {
        checkArgs(2, args);
        if (parser.getTables().containsKey(args[1])) {
            parser.removeTable(args[1]);
            if (parser.getWorkTable().getName().equals(args[1])) {
                parser.setWorkTable(null);
            }
            System.out.println("dropped");
        } else {
            System.out.println(args[1] + " not exists");
        }
    }
}
