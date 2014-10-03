package ru.fizteh.fivt.students.dmitry_persiyanov.shell.commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.shell.Shell;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class CommandMkDir {
    public static void execute(final String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("mkdir: too few arguments");
        } else if (args.length > 1) {
            throw new IllegalArgumentException("mkdir: too many arguments");
        } else {
            try {
                Files.createDirectory(Paths.get(Shell.getWorkingDir(), args[0]));
            } catch (IOException e) {
                throw new IllegalArgumentException("mkdir: cannot create directory '" + args[0] + "': file exists");
            }
        }
    }
}
