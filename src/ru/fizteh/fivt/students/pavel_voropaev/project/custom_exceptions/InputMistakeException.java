package ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions;

public class InputMistakeException extends IllegalArgumentException {

    public InputMistakeException(String string) {
        super(string);
    }
}
