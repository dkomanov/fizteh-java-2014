package ru.fizteh.fivt.students.dsalnikov.shell.commands;

public interface Command {
    void execute(String[] args) throws Exception;

    String getName();

    int getArgsCount();
}
