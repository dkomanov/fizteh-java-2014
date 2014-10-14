package ru.fizteh.fivt.students.RadimZulkarneev.FileMap;

public class MapExcept extends Exception {
    private static final long serialVersionUID = 1L;
    private String messageOfExcept;
    MapExcept(final String problem) {
        messageOfExcept = problem;
    }
    public final String toString() {
        return messageOfExcept;
    }
}


