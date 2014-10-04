package ru.fizteh.fivt.students.ganiev.shell;

import java.io.File;
import java.io.IOException;

public class ChangeDirectory implements Command {
    public void invokeCommand(String[] arguments, Shell.MyShell shell) throws IOException {
        File goToDirectory = FileCommander.getFile(arguments[0], shell);
        if (goToDirectory.isDirectory()) {
            shell.changeCurrentDirectory(goToDirectory.getPath());
        } else {
            throw new IOException("cd: " + arguments[0] + ": No such file or directory");
        }
    }

    public int getNumberOfArguments() {
        return 1;
    }
}