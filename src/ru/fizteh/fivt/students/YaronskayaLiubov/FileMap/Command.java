package ru.fizteh.fivt.students.YaronskayaLiubov.FileMap;

abstract class Command {
    String name;
    int numberOfArguements;

    @Override
    public String toString() {
        return name;
    }

    abstract boolean execute(String[] args);

}
