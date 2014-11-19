package ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.commands.table;

import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.InputMistakeException;
import ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.TableAbstractCommand;
import ru.fizteh.fivt.students.pavel_voropaev.project.master.TableProvider;

import java.io.PrintStream;

public class Remove extends TableAbstractCommand {

    public Remove(TableProvider context) {
        super("remove", 1, context);
    }

    @Override
    public void exec(String[] param, PrintStream out) {
        String retVal = super.getActiveTable().remove(param[0]);
        if (retVal == null) {
            out.println("not found");
        } else {
            out.println("removed");
        }
    }
}
