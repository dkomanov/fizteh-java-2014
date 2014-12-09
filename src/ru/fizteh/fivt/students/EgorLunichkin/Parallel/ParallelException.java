package ru.fizteh.fivt.students.EgorLunichkin.Parallel;

public class ParallelException extends Exception {
    public static void printException(Exception ex) {
        System.err.println(ex.getMessage());
    }

    public ParallelException(String msg) {
        super(msg);
    }
}
