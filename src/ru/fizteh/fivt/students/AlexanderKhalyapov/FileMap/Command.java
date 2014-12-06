package ru.fizteh.fivt.students.AlexanderKhalyapov.FileMap;

import java.io.IOException;
import java.util.Map;

public interface Command {
    String getName();
    void executeCmd(Map<String, String> dataBase, String[] args) throws IOException;
}
