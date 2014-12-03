package ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap;

public class MultiFileHashMapException extends Exception {
    public MultiFileHashMapException(String msg) {
        System.err.println(msg);
        System.exit(1);
    }
}
