package ru.fizteh.fivt.students.pavel_voropaev.project.commands.database;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.pavel_voropaev.project.commands.DatabaseAbstractCommand;
import ru.fizteh.fivt.students.pavel_voropaev.project.commands.DatabaseInterpreterState;
import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.InputMistakeException;

import java.io.PrintStream;
import java.util.List;

public class ShowTables extends DatabaseAbstractCommand {

    public ShowTables(DatabaseInterpreterState state) {
        super("show", 1, state);
    }

    @Override
    public void exec(String[] param) {
        if (!param[0].equals("tables")) {
            throw new InputMistakeException("No such command: show " + param[0]);
        }

        TableProvider database = state.getDatabase();
        List<String> retVal = state.getDatabase().getTableNames();

        PrintStream out = state.getOutputStream();
        for (String entry : retVal) {
            out.println(entry + " " + database.getTable(entry).size());
        }
    }
}
