package ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.MultiFileHashMapCommands;

import java.io.IOException;

import ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.MultiFileHashMapManager;

public class DropCommand extends MultiFileHashMapCommand {
    @Override
    public final void execute(final String[] args,
            final MultiFileHashMapManager parser) throws IOException {
        checkArgs(2, args);
        if (parser.getTables().get(args[1]) != null) {
            parser.getTables().remove(args[1]);
            System.out.println("dropped");
        } else {
            System.out.println(args[1] + " not exists");
        }
    }
}
