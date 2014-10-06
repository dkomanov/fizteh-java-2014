package ru.fizteh.fivt.students.ganiev.shell;

import java.io.File;
import java.io.IOException;

public class Remove implements Command {

    private final int numberOfArguments;

    public Remove(int numberOfArguments) {
        this.numberOfArguments = numberOfArguments;
    }

    public void invokeCommand(String[] arguments, Shell.MyShell shell) throws IOException {
        File source = FileCommander.getFile(arguments[numberOfArguments - 1], shell);
        if (source.exists()) {
            if (numberOfArguments == 1) {
                if (source.isFile()) {
                    source.delete();
                } else {
                    throw new IOException("rm: " + arguments[numberOfArguments - 1] + ": is a directory");
                }
            } else {
                FileCommander.deleteFileOrDirectory(source);
            }
        } else {
            throw new IOException("rm: cannot remove " 
                    + arguments[numberOfArguments - 1] + ": No such file or directory");
        }
    }

    public int getNumberOfArguments() {
        return numberOfArguments;
    }
}
