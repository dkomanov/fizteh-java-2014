package ru.fizteh.fivt.students.dsalnikov.utils;

public class NoTableException extends RuntimeException {
    @Override
    public String getMessage() {
        return "no table";
    }
}
