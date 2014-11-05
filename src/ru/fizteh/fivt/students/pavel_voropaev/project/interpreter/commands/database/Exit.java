package ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.commands.database;

import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.*;
import ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.AbstractCommand;
import ru.fizteh.fivt.students.pavel_voropaev.project.master.Table;
import ru.fizteh.fivt.students.pavel_voropaev.project.master.TableProvider;

import java.io.PrintStream;

public class Exit extends AbstractCommand<TableProvider> {

    public Exit(TableProvider context) {
        super("exit", 0, context);
    }

    @Override
    public void exec(String[] param, PrintStream out) throws InputMistakeException {
        Table activeTable = context.getActiveTable();
        if (activeTable != null && activeTable.getDiff().size() > 0) {
            throw new InputMistakeException("Still have unsaved changes. Use commit or rollback before exit.");
        }
    }

}
