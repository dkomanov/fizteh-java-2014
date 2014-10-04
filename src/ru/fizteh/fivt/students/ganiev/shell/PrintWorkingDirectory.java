package ru.fizteh.fivt.students.ganiev.shell;

public class PrintWorkingDirectory implements Command {
    public void invokeCommand(String[] arguments, Shell.MyShell shell) {
        System.out.println(shell.getCurrentDirectory());
    }

    public int getNumberOfArguments() {
        return 0;
    }
}