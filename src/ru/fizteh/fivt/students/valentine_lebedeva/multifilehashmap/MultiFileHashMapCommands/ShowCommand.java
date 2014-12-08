package ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.MultiFileHashMapCommands;

import java.io.IOException;
import java.util.Map.Entry;

import ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.MultiFileHashMapManager;
import ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.MultiFileTable;

public final class ShowCommand extends MultiFileHashMapCommand {
    @Override
    public void execute(final String[] args, final MultiFileHashMapManager manager) throws IOException {
        checkArgs(2, args);
        if (!args[1].equals("tables")) {
            throw new IllegalArgumentException("Wrong arguments");
        }
        for (Entry<String, MultiFileTable> entry : manager.getTables().entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue().getBase().size());
        }
    }

}
