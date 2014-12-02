package ru.fizteh.fivt.students.EgorLunichkin.JUnit;

public class ExitCommand implements JUnitCommand {
    public ExitCommand() {}

    @Override
    public void run() {
        System.exit(0);
    }
}
