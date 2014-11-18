package ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions;

public class TableDoesNotExistException extends IllegalStateException {

    public TableDoesNotExistException(String string) {
        super("No table: " + string);
    }
}
