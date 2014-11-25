package ru.fizteh.fivt.students.PotapovaSofia.JUnit;

import java.io.IOException;

public class DataBaseIOException extends IOException {
    //private static final long serialVersionUID = 3666497311296045149L;
    public DataBaseIOException(String message, Exception cause) {
        super(message, cause);
    }
}