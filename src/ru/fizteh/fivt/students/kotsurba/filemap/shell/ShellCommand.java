package ru.fizteh.fivt.students.kotsurba.filemap.shell;

public interface ShellCommand {
    void run();

    boolean isMyCommand(CommandString command);
}
