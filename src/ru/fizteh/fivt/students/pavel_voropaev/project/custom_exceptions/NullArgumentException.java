package ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions;

public class NullArgumentException extends IllegalArgumentException {
    public NullArgumentException(String command) {
        super(command + " : null argument");
    }
}
