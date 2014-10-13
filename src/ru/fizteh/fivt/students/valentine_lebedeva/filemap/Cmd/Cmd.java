package ru.fizteh.fivt.students.valentine_lebedeva.filemap.Cmd;

import java.io.IOException;

import ru.fizteh.fivt.students.valentine_lebedeva.filemap.DB;

public interface Cmd {
    String getName();
    void execute(
            DB dataBase, String[] args) throws IOException;
}

