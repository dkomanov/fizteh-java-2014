package ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions;

public class ContainsWrongFilesException extends IllegalArgumentException {

    public ContainsWrongFilesException(String string) {
        super(string + " contains wrong files.");
    }
}
