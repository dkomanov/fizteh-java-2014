package ru.fizteh.fivt.students.ganiev.shell;

import java.io.File;
import java.io.IOException;

public class Copy implements Command {

    private final int numberOfArguments;

    public Copy(int numberOfArguments) {
        this.numberOfArguments = numberOfArguments;
    }

    public void invokeCommand(String[] arguments, Shell.MyShell shell) throws IOException {
        File source = FileCommander.getFile(arguments[numberOfArguments - 2], shell);
        File destination = FileCommander.getFile(arguments[numberOfArguments - 1], shell);

        if (source.exists()) {
            if ((numberOfArguments == 2) && (source.isDirectory())) {
                throw new IOException("cp: " + source.getPath() + " is a directory (not copied).");
            }
            if ((destination.exists()) || ((source.isFile()) && ((new File(destination.getParent())).exists()))) {
                FileCommander.copyFileOrDirectory(source, destination);
            } else {
                throw new IOException("cp: cannot remove " + destination.getPath() + ": No such file or directory");
            }
        } else {
            throw new IOException("cp: cannot remove " + source.getPath() + ": No such file or directory");
        }
    }

    public int getNumberOfArguments() {
        return numberOfArguments;
    }
}
