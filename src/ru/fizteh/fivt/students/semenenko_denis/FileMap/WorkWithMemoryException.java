package ru.fizteh.fivt.students.semenenko_denis.FileMap;

/**
 * Created by denny_000 on 08.10.2014.
 */
public class WorkWithMemoryException extends Exception{
    public WorkWithMemoryException(String message) {
        super(message);
    }

    public WorkWithMemoryException(String message, Exception ex) {
        super(message, ex);
    }
}
