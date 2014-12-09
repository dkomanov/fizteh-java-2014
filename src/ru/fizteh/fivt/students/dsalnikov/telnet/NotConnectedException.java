package ru.fizteh.fivt.students.dsalnikov.telnet;

public class NotConnectedException extends RuntimeException {
    @Override
    public String getMessage() {
        return "not connected";
    }
}
