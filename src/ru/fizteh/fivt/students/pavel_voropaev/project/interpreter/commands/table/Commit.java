package ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.commands.table;

import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.InputMistakeException;
import ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.TableAbstractCommand;
import ru.fizteh.fivt.students.pavel_voropaev.project.master.TableProvider;

import java.io.IOException;
import java.io.PrintStream;

public class Commit extends TableAbstractCommand {

    public Commit(TableProvider context) {
        super("commit", 0, context);
    }

    @Override
    public void exec(String[] param, PrintStream out) {
        try {
            out.println(super.getActiveTable().commit());
        } catch (IOException e) {
            throw new RuntimeException("Commit failed (cannot write to the disk): " + e.getMessage());
        }
    }
}
