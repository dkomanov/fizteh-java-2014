package ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.commands.database;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.InputMistakeException;
import ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.AbstractCommand;
import ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.DatabaseInterpreterState;

import java.io.PrintStream;
import java.util.List;

public class ShowTables extends AbstractCommand {

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
