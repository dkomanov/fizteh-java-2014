package ru.fizteh.fivt.students.YaronskayaLiubov.Telnet;

import ru.fizteh.fivt.storage.structured.Table;

import java.io.File;
import java.io.IOException;

/**
 * Created by luba_yaronskaya on 18.10.14.
 */
public class DropCommand extends TelnetCommand {
    DropCommand() {
        name = "drop";
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
        Table db = tableProvider.getTable(args[1]);

        if (db == null) {
            throw new CommandRuntimeException(args[1] + " not exists");
        }
        if (tableProvider.getOpenTable() == db) {
            tableProvider.setOpenTable(null);
        }
try {
    tableProvider.removeTable(args[1]);
} catch (IOException e) {
    throw new CommandRuntimeException(e.getMessage());
}
        return "dropped";
    }

    static void fileDelete(File myDir) {
        if (myDir.isDirectory()) {
            File[] content = myDir.listFiles();
            for (int i = 0; i < content.length; ++i) {
                fileDelete(content[i]);
            }
        }
        myDir.delete();
    }
}
