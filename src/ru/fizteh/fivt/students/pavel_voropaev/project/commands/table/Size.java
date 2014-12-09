package ru.fizteh.fivt.students.pavel_voropaev.project.commands.table;

import ru.fizteh.fivt.students.pavel_voropaev.project.commands.DatabaseInterpreterState;
import ru.fizteh.fivt.students.pavel_voropaev.project.commands.TableAbstractCommand;

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
