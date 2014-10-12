package ru.fizteh.fivt.students.ElinaDenisova.FileMap;

public class DataBaseException extends Exception {
    private String problem;
    DataBaseException(String problem) {
        this.problem = problem;
    }
    public String toString() {
        return problem;
    }
}


