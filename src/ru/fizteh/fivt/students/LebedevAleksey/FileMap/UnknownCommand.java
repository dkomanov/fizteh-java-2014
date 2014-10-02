package ru.fizteh.fivt.students.LebedevAleksey.FileMap;

public class UnknownCommand extends DatabaseException{
    public UnknownCommand(String message) {
        super(message);
    }

    public UnknownCommand() {
        super("This command is unknown.");
    }

    public UnknownCommand(String message, Throwable ex) {
        super(message, ex);
    }
}
