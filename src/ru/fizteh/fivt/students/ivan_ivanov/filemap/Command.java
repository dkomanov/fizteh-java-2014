package ru.fizteh.fivt.students.ivan_ivanov.filemap;

import java.io.IOException;
import java.util.Map;

public interface Command {
    String getName();

    void executeCmd(Map<String, String> dataBase, String[] args) throws IOException;
}
