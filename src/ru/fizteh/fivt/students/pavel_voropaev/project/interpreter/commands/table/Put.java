package ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.commands.table;

import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.InputMistakeException;
import ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.TableAbstractCommand;
import ru.fizteh.fivt.students.pavel_voropaev.project.master.TableProvider;

import java.io.PrintStream;

public class Put extends TableAbstractCommand {

    public Put(TableProvider context) {
        super("put", 2, context);
    }

    @Override
    public void exec(String[] param, PrintStream out) throws InputMistakeException {
       String retVal = super.getActiveTable().put(param[0], param[1]);
       if (retVal == null) {
           out.println("new");
       } else {
           out.println("overwrite");
           out.println(retVal);
       }
       
    }

}
