package ru.fizteh.fivt.students.vadim_mazaev.multifilemap.commands;

import java.io.IOException;

import ru.fizteh.fivt.students.vadim_mazaev.multifilemap.ThrowExit;
import ru.fizteh.fivt.students.vadim_mazaev.multifilemap.TableManager;

public final class ExitCommand {
    private ExitCommand() {
        //Not called, only for checkstyle.
    }

    public static void execute(TableManager manager) throws ThrowExit {
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
}
