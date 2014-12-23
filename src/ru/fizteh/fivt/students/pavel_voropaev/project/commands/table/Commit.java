package ru.fizteh.fivt.students.pavel_voropaev.project.commands.table;

import ru.fizteh.fivt.students.pavel_voropaev.project.commands.DatabaseInterpreterState;
import ru.fizteh.fivt.students.pavel_voropaev.project.commands.TableAbstractCommand;

import java.io.IOException;

public class Commit extends TableAbstractCommand {

    public Commit(DatabaseInterpreterState state) {
        super("commit", 0, state);
    }

    @Override
    public void exec(String[] param) {
        isTableAvailable();

        try {
            state.getOutputStream().println(state.getActiveTable().commit());
        } catch (IOException e) {
            throw new RuntimeException("Commit failed (cannot write to the disk): " + e.getMessage());
        }
    }
}
