package ru.fizteh.fivt.students.pavel_voropaev.project.commands.database;

import ru.fizteh.fivt.students.pavel_voropaev.project.commands.DatabaseAbstractCommand;
import ru.fizteh.fivt.students.pavel_voropaev.project.commands.DatabaseInterpreterState;
import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.InputMistakeException;

public class Exit extends DatabaseAbstractCommand {

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
