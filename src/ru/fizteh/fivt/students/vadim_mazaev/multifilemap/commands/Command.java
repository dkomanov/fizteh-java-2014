package ru.fizteh.fivt.students.vadim_mazaev.multifilemap.commands;

public interface Command {
    String getName();

    boolean checkArgs(int argLen);

    void execute(String[] cmdWithArgs) throws Exception;
}
