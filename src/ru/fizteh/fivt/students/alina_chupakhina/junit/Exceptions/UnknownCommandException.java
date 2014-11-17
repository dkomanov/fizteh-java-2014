package ru.fizteh.fivt.students.alina_chupakhina.junit.Exceptions;

public class UnknownCommandException extends IllegalArgumentException {

    public UnknownCommandException(String s) {
        super(s);
    }
}