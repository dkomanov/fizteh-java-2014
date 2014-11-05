package ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.commands.database;

import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.*;
import ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.AbstractCommand;
import ru.fizteh.fivt.students.pavel_voropaev.project.master.TableProvider;

import java.io.PrintStream;

public class Drop extends AbstractCommand<TableProvider> {

    public Drop(TableProvider context) {
        super("drop", 1, context);
    }

    @Override
    public void exec(String[] param, PrintStream out) {
        try {
            context.removeTable(param[0]);
        } catch (TableDoesNotExistException e) {
            out.println(param[0] + " not exists");
        }

        out.println("dropped");
    }

}
