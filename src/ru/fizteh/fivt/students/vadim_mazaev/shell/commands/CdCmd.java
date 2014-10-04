package ru.fizteh.fivt.students.vadim_mazaev.shell.commands;

import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;

public final class CdCmd {
    private CdCmd() {
        //not called
    }
    public static void run(final String[] cmdWithArgs) throws Exception {
        if (cmdWithArgs.length == 1) {
            throw new Exception(getName() + ": missing operand");
        } else if (cmdWithArgs.length > 2) {
            throw new Exception(getName()
                    + ": too much arguments");
        }
        try {
            File newWorkingDir = Paths.get(cmdWithArgs[1]).normalize().toFile();
            if (!newWorkingDir.isAbsolute()) {
                newWorkingDir = Paths.get(System.getProperty("user.dir"),
                                newWorkingDir.getPath()).normalize().toFile();
            }
            if (newWorkingDir.exists()) {
                if (newWorkingDir.isDirectory()) {
                    System.setProperty("user.dir", newWorkingDir.getPath());
                } else {
                    throw new Exception(getName() + ": '"
                        + cmdWithArgs[1] + "': is not a directory");
                }
            } else {
                throw new Exception(getName() + ": '"
                    + cmdWithArgs[1] + "': No such file or directory");
            }
        } catch (InvalidPathException e) {
            throw new Exception(getName()
                    + ": cannot change directory to '"
                    + cmdWithArgs[1] + "': illegal character in path");
        } catch (SecurityException e) {
            throw new Exception(getName()
                    + ": cannot change directory: access denied");
        }
    }
    public static String getName() {
        return "cd";
    }
}
