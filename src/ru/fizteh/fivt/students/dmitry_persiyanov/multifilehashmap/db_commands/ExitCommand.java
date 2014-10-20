package ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.DbManager;
import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.exceptions.IllegalNumberOfArgumentsException;

import java.io.IOException;

public class ExitCommand extends DbCommand {
    public ExitCommand(final String[] args) {
        super("exit", args);
        NUM_OF_ARGS = 0;
        checkArgs();
    }

    @Override
    public void execute(final DbManager db) throws IOException {
        db.getCurrentTable().dump();
        System.exit(0);
    }
}
