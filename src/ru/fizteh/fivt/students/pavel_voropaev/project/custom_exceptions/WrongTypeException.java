package ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions;

public class WrongTypeException extends InputMistakeException {

    public WrongTypeException(String string) {
        super("wrong type (" + string + ")");
    }
}
