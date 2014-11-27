package ru.fizteh.fivt.students.valentine_lebedeva.filemap.Cmd;

import java.io.IOException;

import ru.fizteh.fivt.students.valentine_lebedeva.filemap.DB;

public abstract class Command {
    public abstract void execute(DB dataBase, String[] args) throws IOException;

    public static void checkArgs(final int count, final String[] args) {
        if (args.length != count) {
            throw new IllegalArgumentException("Wrong number of arguments");
        }
    }
}
