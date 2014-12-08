package ru.fizteh.fivt.students.kotsurba.filemap.shell;

public class InvalidCommandException extends Error {
    public InvalidCommandException(final String message) {
        super("Invalid command: " + message);
    }
}
