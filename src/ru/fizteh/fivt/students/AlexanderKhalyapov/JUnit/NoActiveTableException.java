package ru.fizteh.fivt.students.AlexanderKhalyapov.JUnit;

public class NoActiveTableException extends IllegalStateException {
    public NoActiveTableException() {
        super("no table");
    }
}
