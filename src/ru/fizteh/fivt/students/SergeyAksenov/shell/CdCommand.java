package ru.fizteh.fivt.students.SergeyAksenov.shell;

import java.io.File;
import java.io.IOException;


public class CdCommand implements Command {

    public void run(final String[] args, final Environment env)
            throws ShellException {
        if (!Executor.checkArgNumber(2, args.length, 2)) {
            ErrorHandler.countArguments("cat");
        }
        File currentFile;
        if (args[1].charAt(0) == '/') {
            currentFile = new File(args[1]);
        } else {
            currentFile = new File(env.currentDirectory
                    + File.separator + args[1]);
        }
        try {
            if (currentFile.exists() && currentFile.isDirectory()) {
                System.setProperty("user.dir", currentFile.getCanonicalPath());
                env.currentDirectory = System.getProperty("user.dir");
            } else {
                throw new IOException();
            }
        } catch (IOException e) {
            ErrorHandler.noFile("cd", args[1]);
        }
    }
}
