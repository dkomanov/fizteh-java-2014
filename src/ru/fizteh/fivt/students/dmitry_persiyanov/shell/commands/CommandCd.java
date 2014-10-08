package ru.fizteh.fivt.students.dmitry_persiyanov.shell.commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.shell.Shell;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class CommandCd {
    public static void execute(final String[] args) {
        if (args.length == 0) {
            Shell.setWorkingDir(System.getProperty("user.dir"));
        } else if (args.length > 1) {
            throw new IllegalArgumentException("cd: Too many arguments");
        } else {
            try {
                Path newWorkingDir;
                if (Paths.get(args[0]).isAbsolute()) {
                    newWorkingDir = Paths.get(args[0]);
                } else {
                    newWorkingDir = Paths.get(Shell.getWorkingDir(), args[0]).normalize();
                }
                if (Files.isDirectory(newWorkingDir)) {
                    Shell.setWorkingDir(newWorkingDir.toAbsolutePath().toString());
                } else {
                    throw new InvalidPathException("", "");
                }
            } catch (InvalidPathException e) {
                throw new IllegalArgumentException("cd: '" + args[0] + "': No such file or directory");
            }
        }
    }
}
