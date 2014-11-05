package ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.commands.database;

import ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.AbstractCommand;
import ru.fizteh.fivt.students.pavel_voropaev.project.master.TableProvider;

import java.io.PrintStream;

public class Size extends AbstractCommand<TableProvider> {

    public Size(TableProvider context) {
        super("size", 0, context);
    }

    @Override
    public void exec(String[] param, PrintStream out) {
        int retVal = 0;
        for (String entry : context.getTablesList()) {
            retVal += context.getTable(entry).size();
        }
        
        out.println(retVal);
    }

}
