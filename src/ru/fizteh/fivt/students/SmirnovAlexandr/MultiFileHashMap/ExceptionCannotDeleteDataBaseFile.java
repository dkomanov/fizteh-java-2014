package ru.fizteh.fivt.students.SmirnovAlexandr.MultiFileHashMap;

public class ExceptionCannotDeleteDataBaseFile extends Exception {
    public final String message;
    ExceptionCannotDeleteDataBaseFile(String childNameConstructor) {
        message = childNameConstructor;
    }
}
