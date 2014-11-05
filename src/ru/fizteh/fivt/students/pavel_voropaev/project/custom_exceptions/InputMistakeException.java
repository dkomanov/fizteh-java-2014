package ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions;

import java.io.IOException;

public class InputMistakeException extends IOException {

    public InputMistakeException(String string) {
        super(string);
    }
}
