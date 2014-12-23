package ru.fizteh.fivt.students.YaronskayaLiubov.Telnet;

import java.io.IOException;

/**
 * Created by luba_yaronskaya on 18.10.14.
 */
public class ExitCommand extends TelnetCommand {
    ExitCommand() {
        name = "exit";
        numberOfArguments = 1;
    }

    @Override
    String execute(RemoteDataTableProvider tableProvider, String[] args) {
        if (args.length != numberOfArguments) {
            throw new CommandRuntimeException(name + ": wrong number of arguments");
        }
        if (tableProvider.getOpenTable() != null) {
            int unsavedChanges = tableProvider.getOpenTable().unsavedChangesCount();
            if (unsavedChanges > 0) {
                throw new CommandRuntimeException(unsavedChanges + " unsaved changes");
            }
        }
        try {
            tableProvider.close();
        } catch (IOException e) {
            throw new CommandRuntimeException(e.getMessage());
        }
        System.exit(0);
        return null;
    }
}
