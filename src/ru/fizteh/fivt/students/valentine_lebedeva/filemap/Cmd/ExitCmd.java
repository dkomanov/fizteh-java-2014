package ru.fizteh.fivt.students.valentine_lebedeva.filemap.Cmd;

import java.io.IOException;

import ru.fizteh.fivt.students.valentine_lebedeva.filemap.DB;

public class ExitCmd implements Cmd {
    public final String getName() {
        return "exit";
    }
    public final void execute(final DB dataBase
            , final String[] args) throws IOException {
        if (args.length != 1) {
            throw new IllegalArgumentException(
                    "Wrong number of arguments");
        }
        dataBase.close();
        System.exit(0);
    }
}
