package ru.fizteh.fivt.students.pavel_voropaev.project.commands.database;

import ru.fizteh.fivt.storage.structured.Table;
import ru.fizteh.fivt.students.pavel_voropaev.project.commands.DatabaseAbstractCommand;
import ru.fizteh.fivt.students.pavel_voropaev.project.commands.DatabaseInterpreterState;
import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.TableDoesNotExistException;

import java.io.IOException;

public class Drop extends DatabaseAbstractCommand {

    public Drop(DatabaseInterpreterState state) {
        super("drop", 1, state);
    }

    @Override
    public void exec(String[] param) throws IOException {
        Table active = state.getActiveTable();
        if (active != null && active.getName().equals(param[0])) {
            state.setActiveTable(null);
        }

        try {
            state.getDatabase().removeTable(param[0]);
        } catch (TableDoesNotExistException e) {
            state.getOutputStream().println(param[0] + " not exists");
            return;
        }

        state.getOutputStream().println("dropped");
    }
}

