package ru.fizteh.fivt.students.torunova.storeable.database.exceptions;

/**
 * Created by nastya on 20.10.14.
 */
public class TableNotCreatedException extends Exception{
    public TableNotCreatedException() {
        super();
    }
    public TableNotCreatedException(String message) {
        super(message);
    }
}
