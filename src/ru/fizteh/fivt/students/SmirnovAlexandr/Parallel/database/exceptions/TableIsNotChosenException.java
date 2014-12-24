package ru.fizteh.fivt.students.SmirnovAlexandr.Parallel.database.exceptions;

import javax.management.AttributeNotFoundException;

public class TableIsNotChosenException extends AttributeNotFoundException {
    public TableIsNotChosenException(final String msg) {
        super("table isn't choosen [" + msg + "]");
    }

    public TableIsNotChosenException() {
        super("table isn't chosen");
    }
}
