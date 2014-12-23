package ru.fizteh.fivt.students.pavel_voropaev.project.commands.table;

import ru.fizteh.fivt.students.pavel_voropaev.project.commands.DatabaseInterpreterState;
import ru.fizteh.fivt.students.pavel_voropaev.project.commands.TableAbstractCommand;

public class Rollback extends TableAbstractCommand {

    public Rollback(DatabaseInterpreterState state) {
        super("rollback", 0, state);
    }

    @Override
    public void exec(String[] param) {
        isTableAvailable();
        state.getOutputStream().println(state.getActiveTable().rollback());
    }
}
