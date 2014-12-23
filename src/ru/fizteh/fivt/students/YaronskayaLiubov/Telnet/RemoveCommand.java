package ru.fizteh.fivt.students.YaronskayaLiubov.Telnet;

import ru.fizteh.fivt.storage.structured.Storeable;

import java.io.IOException;

/**
 * Created by luba_yaronskaya on 19.10.14.
 */
public class RemoveCommand extends TelnetCommand {
    RemoveCommand() {
        name = "remove";
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
            throw new CommandRuntimeException(name + ": wrong number of arguements");
        }
        if (tableProvider.getOpenTable() == null) {
            return "no table";
        }
        Storeable removedRow = tableProvider.getOpenTable().remove(args[1]);
        return (removedRow == null) ? "not found" : "removed";
    }
}
