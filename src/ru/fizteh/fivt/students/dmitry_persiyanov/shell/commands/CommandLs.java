package ru.fizteh.fivt.students.dmitry_persiyanov.shell.commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.shell.Shell;
import java.io.File;

public final class CommandLs {
    public static void execute() {
        File[] dir = new File(Shell.getWorkingDir()).listFiles();
        for (File x : dir) {
            System.out.println(x.getName());
        }
    }
}
