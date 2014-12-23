package ru.fizteh.fivt.students.pavel_voropaev.project.commands.database;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.pavel_voropaev.project.commands.DatabaseAbstractCommand;
import ru.fizteh.fivt.students.pavel_voropaev.project.commands.DatabaseInterpreterState;
import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.InputMistakeException;

public class Use extends DatabaseAbstractCommand {

    public Use(DatabaseInterpreterState state) {
        super("use", 1, state);
    }

    @Override
    public void exec(String[] param) {
        if (!state.isExitSafe()) {
            throw new InputMistakeException(
                    state.getActiveTable().getNumberOfUncommittedChanges() + " unsaved changes");
        }

        Table active = state.getDatabase().getTable(param[0]);
        if (active == null) {
            state.getOutputStream().println(param[0] + " not exists");
        } else {
            state.setActiveTable(active);
            state.getOutputStream().println("using " + param[0]);
        }
    }
}
