package ru.fizteh.fivt.students.dsalnikov.shell.Commands;

public interface Command {
    void execute(String[] args) throws Exception;

    String getName();

    int getArgsCount();
}
