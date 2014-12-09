package ru.fizteh.fivt.students.anastasia_ermolaeva.proxy.commands;


public interface Command {
    public String getName();
    //public String[] checkArguments(final String[] arguments);

    public void run(final String[] arguments);
}
