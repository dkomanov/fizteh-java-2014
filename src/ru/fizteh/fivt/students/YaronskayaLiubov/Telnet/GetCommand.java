package ru.fizteh.fivt.students.YaronskayaLiubov.Telnet;

import ru.fizteh.fivt.storage.structured.Storeable;

import java.io.IOException;

/**
 * Created by luba_yaronskaya on 19.10.14.
 */
public class GetCommand extends TelnetCommand {
    GetCommand() {
        name = "get";
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
        if (tableProvider.getOpenTable() == null) {
            throw new CommandRuntimeException("no table");
        }
        Storeable row = tableProvider.getOpenTable().get(args[1]);
        if (row == null) {
            return "not found";
        }
        return tableProvider.serialize(tableProvider.getOpenTable(), row);
    }
}
