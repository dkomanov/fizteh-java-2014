package ru.fizteh.fivt.students.YaronskayaLiubov.Telnet;

import java.io.IOException;

/**
 * Created by luba_yaronskaya on 10.11.14.
 */
public class RollbackCommand extends TelnetCommand {
    RollbackCommand() {
        name = "rollback";
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
            throw new CommandRuntimeException(name + ": wrong number of arguements");
        }
        if (tableProvider.getOpenTable() == null) {
            return "no table";
        }
        return Integer.toString(tableProvider.getOpenTable().rollback());
    }
}
