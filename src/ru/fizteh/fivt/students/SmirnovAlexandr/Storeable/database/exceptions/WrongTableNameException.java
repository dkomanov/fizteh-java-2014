package ru.fizteh.fivt.students.SmirnovAlexandr.Storeable.database.exceptions;


public class WrongTableNameException extends IllegalArgumentException {
    public WrongTableNameException() {
        super();
    }

    public WrongTableNameException(final String msg) {
        super("name \"" + msg + "\" is illegal");
    }
}
