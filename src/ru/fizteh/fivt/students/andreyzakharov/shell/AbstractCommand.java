package ru.fizteh.fivt.students.andreyzakharov.shell;

public abstract class AbstractCommand {
    Shell shell;
    String identifier;

    public AbstractCommand(Shell shell) {
        this.shell = shell;
    }

    public abstract void execute(String... args);

    public String toString() {
        return identifier;
    }
}
