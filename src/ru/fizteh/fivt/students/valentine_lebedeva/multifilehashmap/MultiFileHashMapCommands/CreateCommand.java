package ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.MultiFileHashMapCommands;

import java.io.File;
import java.io.IOException;

import ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.MultiFileHashMapManager;
import ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.MultiFileTable;

public class CreateCommand extends MultiFileHashMapCommand {

    @Override
    public final void execute(final String[] args,
            final MultiFileHashMapManager parser) throws IOException {
        checkArgs(2, args);
        if (!parser.getTables().isEmpty()
                && parser.getTables().size() == MAX_NUMBER_OF_TABLES) {
            throw new IllegalArgumentException("Too many tables");
        }
        if (parser.getTables().get(args[1]) != null) {
            System.out.println(args[1] + " exists");
        } else {
            File path = new File(System.getProperty("fizteh.db.dir"), args[1]);
            path.mkdir();
            MultiFileTable tmp = new MultiFileTable(path.getAbsolutePath());
            parser.putTables(args[1], tmp);
            System.out.println("created");
        }
    }
}
