package ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.commands.table;

import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.InputMistakeException;
import ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.TableAbstractCommand;
import ru.fizteh.fivt.students.pavel_voropaev.project.master.TableProvider;

import java.io.PrintStream;

public class Commit extends TableAbstractCommand {

    public Commit(TableProvider context) {
        super("commit", 0, context);
    }

    @Override
    public void exec(String[] param, PrintStream out) throws InputMistakeException {
            out.println(super.getActiveTable().commit());
    }

}
