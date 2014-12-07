package ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.filemap.Cmd;

import java.io.IOException;

import ru.fizteh.fivt.students.valentine_lebedeva.multifilehashmap.MultiFileHashMapManager;

public abstract class Command {
    public abstract void execute(String[] args, MultiFileHashMapManager manager) throws IOException;

    public static void checkArgs(final int count, final String[] args) {
        if (args.length != count) {
            throw new IllegalArgumentException("Wrong number of arguments");
        }
    }
}
