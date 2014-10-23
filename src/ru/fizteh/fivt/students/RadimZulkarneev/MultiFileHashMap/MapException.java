package ru.fizteh.fivt.students.RadimZulkarneev.MultiFileHashMap;

public class MapException extends Exception {
    private final String messageOfException;
    MapException(final String problem) {
        messageOfException = problem;
    }
    
    public final String toString() {
        return messageOfException;
    }
}

