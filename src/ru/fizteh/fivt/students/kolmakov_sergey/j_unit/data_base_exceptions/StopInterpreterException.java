package ru.fizteh.fivt.students.kolmakov_sergey.j_unit.data_base_exceptions;

public class StopInterpreterException extends Exception {
    public int exitCode;
    public StopInterpreterException(int exitCode){
        this.exitCode = exitCode;
    }
}
