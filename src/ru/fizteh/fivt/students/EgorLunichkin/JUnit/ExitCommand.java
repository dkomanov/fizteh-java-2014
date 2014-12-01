package ru.fizteh.fivt.students.EgorLunichkin.JUnit;

public class ExitCommand implements Command {
    public ExitCommand() {}

    public void run() {
        System.exit(0);
    }
}
