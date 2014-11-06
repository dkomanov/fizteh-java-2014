package ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.test.support;

import ru.fizteh.fivt.students.fedorov_andrew.databaselibrary.shell.Command;

/**
 * Created by phoenix on 06.11.14.
 */
public abstract class AlternativeCommand implements Command<AlternativeShellState> {
    private String name;

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
