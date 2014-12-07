package ru.fizteh.fivt.students.RadimZulkarneev.MultiFileHashMap;

public class TableConnectionException extends Exception {
    private static final long serialVersionUID = 1L;
    private final String messageOfExcept;
    
    TableConnectionException(final String problem) {
        messageOfExcept = problem;
    }
    
    public final String toString() {
        return messageOfExcept;
    }

}
