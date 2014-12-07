package ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions;

public class ContainsWrongFilesException extends IllegalArgumentException {

    public ContainsWrongFilesException(String directory) {
        super(directory + " contains wrong files");
    }
}
