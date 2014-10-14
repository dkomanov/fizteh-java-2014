package ru.fizteh.fivt.students.AndrewFedorov.filemap;

public interface Command {
    void execute(Shell shell, String[] args) throws HandledException;
    String getInfo();
    String getInvocation();
}
