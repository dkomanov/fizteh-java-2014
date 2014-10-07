package ru.fizteh.fivt.students.RadimZulkarneev.FileMap;

public class MapExcept extends Exception {
    private static final long serialVersionUID = 1L;
    private String messageOfExcept;
    MapExcept(final String problem) {
        messageOfExcept = problem;
    }
<<<<<<< HEAD
    public final String toString() {
        return messageOfExcept;
    }
}


=======
    final public String toString() {
        return messageOfExcept;
    }

}
>>>>>>> parent of 7dfbcf9... FileMap commit. Fix
