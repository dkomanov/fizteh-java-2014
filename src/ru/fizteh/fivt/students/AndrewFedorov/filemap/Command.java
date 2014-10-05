package ru.fizteh.fivt.students.AndrewFedorov.filemap;

public interface Command {
<<<<<<< HEAD
    void execute(Shell shell, String[] args) throws HandledException;
    String getInfo();
    String getInvocation();
=======
    public void execute(Shell shell, String[] args) throws HandledException;

    public String getInfo();

    public String getInvocation();
>>>>>>> Fixed checkstyle
}

