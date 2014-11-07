package ru.fizteh.fivt.students.anastasia_ermolaeva.junit.util;

public class NoActiveTableException extends IllegalStateException {
    public NoActiveTableException() {
        super("no table");
    }
}
