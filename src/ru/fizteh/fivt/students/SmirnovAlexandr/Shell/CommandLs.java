package ru.fizteh.fivt.students.SmirnovAlexandr.Shell;

import java.io.File;

public final class CommandLs {
    public static void execute(final String[] args) {
        if (args.length > 0) {
            throw new IllegalArgumentException("ls: parameters are not allowed");
        }
        File[] dir = new File(Shell.getWorkingDir()).listFiles();
        for (File x : dir) {
            System.out.println(x.getName());
        }
    }
}
