package ru.fizteh.fivt.students.AlexanderKhalyapov.Storeable;

public class NoActiveTableException extends IllegalStateException {
    public NoActiveTableException() {
        super("no table");
    }
}
