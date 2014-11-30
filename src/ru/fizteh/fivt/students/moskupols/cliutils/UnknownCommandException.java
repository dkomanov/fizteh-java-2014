package ru.fizteh.fivt.students.moskupols.cliutils;

/**
 * Created by moskupols on 28.09.14.
 */
public class UnknownCommandException extends Exception {
    public UnknownCommandException(String unknown) {
        super("Unknown command '" + unknown + "'");
    }
}
