package ru.fizteh.fivt.students.kolmakov_sergey.j_unit.interpreter;

public class StopInterpreterException extends Exception {
    public final int exitCode;
    public StopInterpreterException(int exitCode) {
        this.exitCode = exitCode;
    }
}
