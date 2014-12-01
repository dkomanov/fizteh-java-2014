package ru.fizteh.fivt.students.titov.shell;

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
