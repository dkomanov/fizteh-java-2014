package ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.commands.table;

import ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.DatabaseInterpreterState;
import ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.TableAbstractCommand;

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
