package ru.fizteh.fivt.students.AlexanderKhalyapov.Proxy;

public class NoActiveTableException extends IllegalStateException {
    public NoActiveTableException() {
        super("no table");
    }
}
