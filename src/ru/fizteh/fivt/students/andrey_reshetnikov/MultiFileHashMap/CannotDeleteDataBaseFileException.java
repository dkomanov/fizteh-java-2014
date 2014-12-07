package ru.fizteh.fivt.students.andrey_reshetnikov.MultiFileHashMap;

public class CannotDeleteDataBaseFileException extends Exception{
    public final String message;
    CannotDeleteDataBaseFileException(String childNameConstructor) {
        message = childNameConstructor;
    }
}
