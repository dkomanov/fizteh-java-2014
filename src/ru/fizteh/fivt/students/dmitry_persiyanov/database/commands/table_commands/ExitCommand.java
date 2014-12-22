package ru.fizteh.fivt.students.dmitry_persiyanov.database.commands.table_commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.database.commands.DbCommand;
import ru.fizteh.fivt.students.dmitry_persiyanov.database.db_table_provider.DbTableProvider;
import ru.fizteh.fivt.students.dmitry_persiyanov.interpreter.TerminateInterpeterException;

import java.io.IOException;
import java.io.PrintStream;

public class ExitCommand extends DbCommand {
    public ExitCommand(final String[] args, final DbTableProvider tableProvider) {
        super("exit", 0, args, tableProvider);
    }

    @Override
    protected void execChecked(final PrintStream out) throws IOException {
        if (currentTable() != null) {
            currentTable().commit();
        }
        throw new TerminateInterpeterException(0);
    }
}
