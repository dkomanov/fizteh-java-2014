package ru.fizteh.fivt.students.EgorLunichkin.Parallel;

public class ParallelException extends Exception {
    public ParallelException(String msg) {
        System.err.println(msg);
    }
}
