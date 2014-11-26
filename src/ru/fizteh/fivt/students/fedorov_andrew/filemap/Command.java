package ru.fizteh.fivt.students.fedorov_andrew.filemap;

public interface Command {
    void execute(Shell shell, String[] args) throws HandledException;

    String getInfo();

    String getInvocation();
}
