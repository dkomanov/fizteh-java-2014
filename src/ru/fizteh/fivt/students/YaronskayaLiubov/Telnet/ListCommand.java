package ru.fizteh.fivt.students.YaronskayaLiubov.Telnet;

import java.io.IOException;

/**
 * Created by luba_yaronskaya on 19.10.14.
 */
public class ListCommand extends TelnetCommand {
    ListCommand() {
        name = "list";
        numberOfArguments = 1;
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
        if (tableProvider.getOpenTable() == null) {
            throw new CommandRuntimeException("no table");
        }

        return String.join(", ", tableProvider.getOpenTable().list());
    }
}
