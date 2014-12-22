package ru.fizteh.fivt.students.gudkov394.telnet;

public class ColumnFormatException extends IllegalArgumentException {

    public ColumnFormatException() {
    }

    public ColumnFormatException(String s) {
        super(s);
    }

    public ColumnFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public ColumnFormatException(Throwable cause) {
        super(cause);
    }
}
