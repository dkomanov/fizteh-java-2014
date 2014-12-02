package ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.exceptions;

import javax.management.AttributeNotFoundException;

public class TableIsNotChosenException extends AttributeNotFoundException {
    public TableIsNotChosenException(final String msg) {
        super("table isn't choosen [" + msg + "]");
    }

    public TableIsNotChosenException() {
        super("table isn't chosen");
    }
}
