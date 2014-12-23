package ru.fizteh.fivt.students.YaronskayaLiubov.parallel;

abstract class Command {
    String name;
    int numberOfArguments;

    @Override
    public String toString() {
        return name;
    }

    abstract boolean execute(MultiFileHashMap multiFileHashMap, String[] args) throws MultiFileMapRunTimeException;

}

