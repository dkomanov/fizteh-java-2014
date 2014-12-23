package ru.fizteh.fivt.students.anastasia_ermolaeva.util.exceptions;

public class NoActiveTableException extends IllegalStateException {
    public NoActiveTableException() {
        super("no table");
    }
}
