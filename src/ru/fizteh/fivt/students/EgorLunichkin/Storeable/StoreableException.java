package ru.fizteh.fivt.students.EgorLunichkin.Storeable;

public class StoreableException extends Exception {
    public StoreableException(String msg) {
        System.err.println(msg);
        System.exit(1);
    }
}
