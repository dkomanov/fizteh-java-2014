package ru.fizteh.fivt.students.ganiev.shell;

public class Exit implements Command {
    public void invokeCommand(String[] arguments, Shell.MyShell shell) throws BreakingException {
        throw new BreakingException();
    }

    public int getNumberOfArguments() {
        return 0;
    }
}