package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support;

import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell.Command;

public abstract class AlternativeCommand implements Command<AlternativeShellState> {
    private final String name;

    public AlternativeCommand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getInfo() {
        return null;
    }

    @Override
    public String getInvocation() {
        return null;
    }
}
