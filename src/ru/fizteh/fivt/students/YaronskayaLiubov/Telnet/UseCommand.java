package ru.fizteh.fivt.students.YaronskayaLiubov.Telnet;

import java.io.IOException;

/**
 * Created by luba_yaronskaya on 18.10.14.
 */
public class UseCommand extends TelnetCommand {
    UseCommand() {
        name = "use";
        numberOfArguments = 2;
    }

    @Override
    String execute(RemoteDataTableProvider tableProvider, String[] args) {
        if (tableProvider.isConnected()) {
            try {
                return tableProvider.sendCmd(serializeCmd(args));
            } catch (IOException e) {
                throw new CommandRuntimeException(name + ": connection error");
            }
        }

        if (args.length != numberOfArguments) {
            throw new CommandRuntimeException(name + ": wrong number of arguments");
        }

        String tableName = args[1];
        StoreableDataTable table = (StoreableDataTable) tableProvider.getTable(tableName);
        if (table == null) {
            throw new CommandRuntimeException(tableName + " not exists");
        }
        if (tableProvider.getOpenTable() != null) {
            int unsavedChanges = tableProvider.getOpenTable().unsavedChangesCount();
            if (unsavedChanges > 0) {
                throw new CommandRuntimeException(unsavedChanges + " unsaved changes");
            }
        }

        tableProvider.setOpenTable(table);
        return "using " + tableName;
    }
}
