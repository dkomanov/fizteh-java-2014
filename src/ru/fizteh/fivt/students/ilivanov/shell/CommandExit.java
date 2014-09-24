package ru.fizteh.fivt.students.ilivanov.shell;

public class CommandExit implements Command {
    @Override
    public int execute() {
        Shell.exit();
        return 0;
    }
}
