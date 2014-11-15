package ru.fizteh.fivt.students.dmitry_persiyanov.database.exceptions;

/**
 * Created by drack3800 on 13.11.2014.
 */
public class WrongTableNameException extends IllegalArgumentException {
    public WrongTableNameException() {
        super();
    }

    public WrongTableNameException(final String msg) {
        super("wrong tableName argument: " + msg);
    }
}
