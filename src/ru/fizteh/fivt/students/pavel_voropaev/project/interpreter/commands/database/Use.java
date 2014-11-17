package ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.commands.database;

import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.InputMistakeException;
import ru.fizteh.fivt.students.pavel_voropaev.project.custom_exceptions.TableDoesNotExistException;
import ru.fizteh.fivt.students.pavel_voropaev.project.interpreter.AbstractCommand;
import ru.fizteh.fivt.students.pavel_voropaev.project.master.TableProvider;

import java.io.PrintStream;

public class Use extends AbstractCommand<TableProvider> {

    public Use(TableProvider context) {
        super("use", 1, context);
    }

    @Override
    public void exec(String[] param, PrintStream out) throws InputMistakeException {
        int diffSize = 0;
        if (context.getActiveTable() != null) {
            diffSize = context.getActiveTable().getDiff().size();
        }
        if (diffSize != 0) {
            throw new InputMistakeException(diffSize + " unsaved changes");
        } else {
            try {
                context.setActiveTable(param[0]);
                out.println("using " + param[0]);
            } catch (TableDoesNotExistException e) {
                out.println(param[0] + " not exists");
            }
        }
    }
}
