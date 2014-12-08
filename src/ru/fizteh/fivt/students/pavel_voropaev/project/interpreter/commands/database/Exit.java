package ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.commands.database;

import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.InputMistakeException;
import ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.AbstractCommand;
import ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.DatabaseInterpreterState;

public class Exit extends AbstractCommand {

    public Exit(DatabaseInterpreterState state) {
        super("exit", 0, state);
    }

    @Override
    public void exec(String[] param) {
        if (!state.isExitSafe()) {
            throw new InputMistakeException("Still have unsaved changes. Use commit or rollback before exit");
        }
    }
}
