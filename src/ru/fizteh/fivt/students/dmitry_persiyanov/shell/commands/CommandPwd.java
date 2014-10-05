package ru.fizteh.fivt.students.dmitry_persiyanov.shell.commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.shell.Shell;

public final class CommandPwd {
    public static void execute() {
        System.out.println(Shell.getWorkingDir());
    }
}
