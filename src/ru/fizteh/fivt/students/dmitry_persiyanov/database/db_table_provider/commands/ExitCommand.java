package ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table_provider.commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table_provider.DbTableProvider;
import ru.fizteh.fivt.students.dmitry_persiyanov.interpreter.TerminateInterpeterException;

import java.io.IOException;
import java.io.PrintStream;

public class ExitCommand extends DbTableProviderCommand {
    public ExitCommand(final String[] args, final DbTableProvider relatedDb) {
        super("exit", 0, args, relatedDb);
    }

    @Override
    protected void execute(final PrintStream out) throws IOException {
        relatedDb.dump();
        throw new TerminateInterpeterException(0);
    }
}
