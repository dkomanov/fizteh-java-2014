package ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.commands.table;

import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.InputMistakeException;
import ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.TableAbstractCommand;
import ru.fizteh.fivt.students.pavel_voropaev.project.master.TableProvider;

import java.io.PrintStream;

public class Get extends TableAbstractCommand {

    public Get(TableProvider context) {
        super("get", 1, context);
    }

    @Override
    public void exec(String[] param, PrintStream out) throws InputMistakeException {
       String retVal = super.getActiveTable().get(param[0]);
       if (retVal == null) {
          out.println("not found");
       } else {
           out.println("found");
           out.println(retVal);
       }
       
    }

}
