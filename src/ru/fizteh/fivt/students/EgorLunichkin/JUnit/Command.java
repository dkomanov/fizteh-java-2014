package ru.fizteh.fivt.students.EgorLunichkin.JUnit;

import ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap.Table;

public interface Command {
    void run();
    void runOnTable(Table table);
}
