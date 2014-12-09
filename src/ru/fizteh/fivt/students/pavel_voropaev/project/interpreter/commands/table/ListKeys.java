package ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.commands.table;

import ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.TableAbstractCommand;
import ru.fizteh.fivt.students.pavel_voropaev.project.master.TableProvider;

import java.io.PrintStream;
import java.util.List;

public class ListKeys extends TableAbstractCommand {

    public ListKeys(TableProvider context) {
        super("list", 0, context);
    }

    @Override
    public void exec(String[] param, PrintStream out) {
        List<String> retVal = super.getActiveTable().list();
        String joined = String.join(", ", retVal);
        out.println(joined);
    }
}
