package ru.fizteh.fivt.students.ZatsepinMikhail.shell;

public abstract class Command {
    protected String name;
    protected int numberOfArguments;

    public abstract boolean run(final String[] arguments);

    @Override
    public final String toString() {
        return name;
    }
}
