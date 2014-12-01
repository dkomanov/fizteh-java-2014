package ru.fizteh.fivt.students.MaksimovAndrey.shell;

import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class Instruction {
    protected String nameOfInstruction;
    protected static Path presentDirectory = Paths.get("").toAbsolutePath().normalize();

    public abstract boolean startNeedInstruction(String[] arguments);

    @Override
    public final String toString() {
        return nameOfInstruction;
    }
}

