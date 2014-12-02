package ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap;

public interface Command {
    void run() throws Exception;
    void runOnTable(MultiTable table) throws Exception;
}
