package ru.fizteh.fivt.students.pavel_voropaev.project.interpreter;

import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.InputMistakeException;

public abstract class TableAbstractCommand extends AbstractCommand {

    protected TableAbstractCommand(String name, int argNum, DatabaseInterpreterState state) {
        super(name, argNum, state);
    }

    protected void isTableAvailable() {
        if (state.getActiveTable() == null) {
            throw new InputMistakeException("no table");
        }
    }
}
