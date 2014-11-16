package ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.MultiFileHashMapCommands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.MultiFileHashMapManager;
import ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.MultiFileTable;

public final class CreateCommand extends MultiFileHashMapCommand {
    @Override
    public void execute(final String[] args, MultiFileHashMapManager parser) throws IOException {
        checkArgs(2, args);
        if (!parser.getTables().isEmpty() && parser.getTables().size() == MAX_NUMBER_OF_TABLES) {
            throw new IllegalArgumentException("Too many tables");
        }
        if (Paths.get(args[1]).toString().contains(File.separator)) {
            throw new IllegalArgumentException("Table name contains separators");
        }
        if (parser.getTables().containsKey(args[1])) {
            System.out.println(args[1] + " exists");
        } else {
            File path = new File(System.getProperty("fizteh.db.dir"), args[1]);
            if (!path.mkdir()) {
                throw new IOException("Creation is failed");
            }
            MultiFileTable tmp = new MultiFileTable(path.getAbsolutePath());
            parser.putTable(args[1], tmp);
            System.out.println("created");
        }
    }
}
