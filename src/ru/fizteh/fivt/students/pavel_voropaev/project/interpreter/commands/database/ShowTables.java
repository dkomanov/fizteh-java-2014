package ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.commands.database;

import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.InputMistakeException;
import ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.AbstractCommand;
import ru.fizteh.fivt.students.pavel_voropaev.project.master.TableProvider;

import java.io.PrintStream;
import java.util.List;

public class ShowTables extends AbstractCommand<TableProvider> {

    public ShowTables(TableProvider context) {
        super("show", 1, context);
    }

    @Override
    public void exec(String[] param, PrintStream out) {
        if (!param[0].equals("tables")) {
            throw new InputMistakeException("No such command: show " + param[0]);
        }

        List<String> retVal = context.getTablesList();
        for (String entry : retVal) {
            out.println(entry + " " + context.getTable(entry).size());
        }
    }
}
