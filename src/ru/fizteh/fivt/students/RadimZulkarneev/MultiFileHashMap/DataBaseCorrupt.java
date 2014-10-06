package ru.fizteh.fivt.students.RadimZulkarneev.MultiFileHashMap;

public class DataBaseCorrupt extends Exception {

    private static final long serialVersionUID = 1L;
    private String messageOfExcept;
    DataBaseCorrupt(final String problem) {
        messageOfExcept = problem;
    }
    public final String toString() {
        return messageOfExcept;
    }


}
