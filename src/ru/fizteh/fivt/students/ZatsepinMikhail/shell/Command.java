package ru.fizteh.fivt.students.ZatsepinMikhail.shell;

abstract public class Command {
    protected String name;
    protected int numberOfArguments;

    abstract public boolean run(final String[] arguments);

    final public String getName() {
        return name;
    }

    @Override
    final public String toString() {
        return name;
    }
}
