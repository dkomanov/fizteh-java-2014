package ru.fizteh.fivt.students.anastasia_ermolaeva.proxy.commands;


public interface Command {
    String getName();

    void run(final String[] arguments);
}
