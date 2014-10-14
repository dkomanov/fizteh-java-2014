package ru.fizteh.fivt.students.andreyzakharov.shell;

public abstract class AbstractCommand {
    String identifier;
    Shell shell;

    public AbstractCommand(String identifier, Shell shell) {
        this.identifier = identifier;
        this.shell = shell;
    }

    public abstract void execute(String... args);

    public String toString() {
        return identifier;
    }
}
