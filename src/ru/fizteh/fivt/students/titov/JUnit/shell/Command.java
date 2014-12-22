package ru.fizteh.fivt.students.titov.JUnit.shell;

public abstract class Command {
    protected String name;
    protected int numberOfArguments;

    public abstract boolean run(final String[] arguments);

    public final String getName() {
        return name;
    }

    @Override
    public final String toString() {
        return name;
    }
}
