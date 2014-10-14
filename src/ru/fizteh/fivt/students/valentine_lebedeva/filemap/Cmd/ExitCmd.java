package ru.fizteh.fivt.students.valentine_lebedeva.filemap.Cmd;

import java.io.IOException;

import ru.fizteh.fivt.students.valentine_lebedeva.filemap.DB;

public class ExitCmd extends Cmd {
    public final String getName() {
        return "exit";
    }

    public final void execute(final DB dataBase, final String[] args)
            throws IOException {
        checkArgs(1, args);
        dataBase.close();
        System.exit(0);
    }
}
