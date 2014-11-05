package ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.commands.database;

import ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.AbstractCommand;
import ru.fizteh.fivt.students.pavel_voropaev.project.master.Table;
import ru.fizteh.fivt.students.pavel_voropaev.project.master.TableProvider;

import java.io.PrintStream;

public class Create extends AbstractCommand<TableProvider> {

    public Create(TableProvider context) {
        super("create", 1, context);
    }

    @Override
    public void exec(String[] param, PrintStream out) {
        Table table = context.createTable(param[0]);
        if (table != null) {
            out.println("created");
        } else {
            out.println(param[0] + " exists");
        }
    }
}
