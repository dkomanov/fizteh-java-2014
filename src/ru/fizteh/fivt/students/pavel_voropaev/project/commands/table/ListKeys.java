package ru.fizteh.fivt.students.pavel_voropaev.project.commands.table;

import ru.fizteh.fivt.students.pavel_voropaev.project.commands.DatabaseInterpreterState;
import ru.fizteh.fivt.students.pavel_voropaev.project.commands.TableAbstractCommand;

import java.util.List;

public class ListKeys extends TableAbstractCommand {

    public ListKeys(DatabaseInterpreterState state) {
        super("list", 0, state);
    }

    @Override
    public void exec(String[] param) {
        isTableAvailable();

        List<String> retVal = state.getActiveTable().list();
        String joined = String.join(", ", retVal);
        state.getOutputStream().println(joined);
    }
}
