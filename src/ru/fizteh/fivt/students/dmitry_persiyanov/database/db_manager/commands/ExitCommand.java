package ru.fizteh.fivt.students.dmitry_persiyanov.database.db_manager.commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_manager.DbManager;
import ru.fizteh.fivt.students.dmitry_persiyanov.interpreter.TerminateInterpeterException;

import java.io.IOException;
import java.io.PrintStream;

public class ExitCommand extends DbManagerCommand {
    public ExitCommand(final String[] args, final DbManager relatedDb) {
        super("exit", 0, args, relatedDb);
    }

    @Override
    protected void execute(final PrintStream out) throws IOException {
        relatedDb.dumpCurrentTable();
        throw new TerminateInterpeterException(0);
    }
}
