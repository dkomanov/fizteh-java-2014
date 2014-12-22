package ru.fizteh.fivt.students.AlexanderKhalyapov.Proxy;

public interface Command {
    String getName();

    void run(final String[] arguments);
}
