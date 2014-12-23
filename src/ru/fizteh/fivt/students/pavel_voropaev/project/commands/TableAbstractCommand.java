package ru.fizteh.fivt.students.pavel_voropaev.project.commands;

import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.InputMistakeException;

public abstract class TableAbstractCommand extends DatabaseAbstractCommand {

    protected TableAbstractCommand(String name, int argNum, DatabaseInterpreterState state) {
        super(name, argNum, state);
    }

    protected void isTableAvailable() {
        if (state.getActiveTable() == null) {
            throw new InputMistakeException("no table");
        }
    }
}
