package ru.fizteh.fivt.students.YaronskayaLiubov.JUnit;

abstract class Command {
    String name;
    int numberOfArguments;

    @Override
    public String toString() {
        return name;
    }

    abstract boolean execute(String[] args) throws Exception;

}

