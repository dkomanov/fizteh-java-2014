package ru.fizteh.fivt.students.pavel_voropaev.project.commands;

import ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.AbstractCommand;

public abstract class DatabaseAbstractCommand extends AbstractCommand {
    protected DatabaseInterpreterState state;

    protected DatabaseAbstractCommand(String name, int argNum, DatabaseInterpreterState state) {
        super(name, argNum, state);
        this.state = state;
    }

}
