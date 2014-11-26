package ru.fizteh.fivt.students.ekaterina_pogodina.basicclasses;

public class TableNullNameException extends Exception {
    public TableNullNameException() {
        super("Table name is null");
    }
}
