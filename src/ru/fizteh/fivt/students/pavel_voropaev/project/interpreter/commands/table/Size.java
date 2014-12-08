package ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.commands.table;

import ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.DatabaseInterpreterState;
import ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.TableAbstractCommand;

public class Size extends TableAbstractCommand {

    public Size(DatabaseInterpreterState state) {
        super("size", 0, state);
    }

    @Override
    public void exec(String[] param) {
        isTableAvailable();
        state.getOutputStream().println(state.getActiveTable().size());
    }
}
