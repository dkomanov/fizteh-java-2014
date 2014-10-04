package ru.fizteh.fivt.students.ganiev.shell;

import java.io.File;

public class Ls implements Command {
    public void invokeCommand(String[] arguments, Shell.MyShell shell) {
        for (String currentFile : (new File(shell.getCurrentDirectory())).list()) {
            System.out.println(currentFile);
        }
    }

    public int getNumberOfArguments() {
        return 0;
    }
}