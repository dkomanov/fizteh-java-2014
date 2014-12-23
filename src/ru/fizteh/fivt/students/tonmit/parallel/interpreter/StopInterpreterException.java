package ru.fizteh.fivt.students.tonmit.parallel.interpreter;

class StopInterpreterException extends Exception {
    public final int exitCode;
    public StopInterpreterException(int exitCode) {
        this.exitCode = exitCode;
    }
}
