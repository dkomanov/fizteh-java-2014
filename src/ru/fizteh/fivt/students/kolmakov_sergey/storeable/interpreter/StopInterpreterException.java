package ru.fizteh.fivt.students.kolmakov_sergey.storeable.interpreter;

class StopInterpreterException extends Exception {
    public final int exitCode;
    public StopInterpreterException(int exitCode) {
        this.exitCode = exitCode;
    }
}
