package ru.fizteh.fivt.students.EgorLunichkin.Storeable.commands;

public class ExitCommand implements Command {
    public ExitCommand() {}

    @Override
    public void run() {
        System.exit(0);
    }
}
