package ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.commands.table;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.DatabaseInterpreterState;
import ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.TableAbstractCommand;

public class Remove extends TableAbstractCommand {

    public Remove(DatabaseInterpreterState state) {
        super("remove", 1, state);
    }

    @Override
    public void exec(String[] param) {
        isTableAvailable();

        Storeable retVal = state.getActiveTable().remove(param[0]);
        if (retVal == null) {
            state.getOutputStream().println("not found");
        } else {
            state.getOutputStream().println("removed");
        }
    }
}
