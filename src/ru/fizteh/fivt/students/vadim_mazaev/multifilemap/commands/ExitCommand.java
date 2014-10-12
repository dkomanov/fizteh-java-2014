package ru.fizteh.fivt.students.vadim_mazaev.multifilemap.commands;

import java.io.IOException;

import ru.fizteh.fivt.students.vadim_mazaev.filemap.ThrowExit;
import ru.fizteh.fivt.students.vadim_mazaev.multifilemap.TableManager;

public final class ExitCommand {
    public ExitCommand(TableManager manager) {
        this.manager = manager;
    }

    public void execute(String[] cmdWithArgs) throws ThrowExit {
        if (manager.getUsedTable() != null) {
            try {
                manager.getUsedTable().commit();
            } catch (IOException e) {
                System.err.println("Error writing table to file");
                throw new ThrowExit(false);
            }
        }
        throw new ThrowExit(true);
    }
    private TableManager manager;
}
