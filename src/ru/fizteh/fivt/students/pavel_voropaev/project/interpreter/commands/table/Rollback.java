package ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.commands.table;

import ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.TableAbstractCommand;
import ru.fizteh.fivt.students.pavel_voropaev.project.master.TableProvider;

import java.io.PrintStream;

public class Rollback extends TableAbstractCommand {

    public Rollback(TableProvider context) {
        super("rollback", 0, context);
    }

    @Override
    public void exec(String[] param, PrintStream out) {
        out.println(super.getActiveTable().rollback());
    }
}
