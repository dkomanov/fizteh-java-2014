package ru.fizteh.fivt.students.ZatsepinMikhail.FileMap;

import java.util.HashMap;

public abstract class Command {
    protected String name;
    protected int numberOfArguments;

    public abstract boolean
        run(HashMap<String, String> dataBase, String[] args);

    @Override
    public final String toString() {
        return name;
    }
}
