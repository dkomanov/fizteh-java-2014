package ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.db_commands.db_manager_commands;

import ru.fizteh.fivt.students.dmitry_persiyanov.multifilehashmap.DbManager;

import java.io.IOException;

public class ExitCommand extends DbManagerCommand {
    public ExitCommand(final String[] args) {
        super("exit", args);
        numOfArgs = 0;
        checkArgs();
    }

    @Override
    public void execute(final DbManager dbManager) throws IOException {
        dbManager.dumpCurrentTable();
        System.exit(0);
    }
}
