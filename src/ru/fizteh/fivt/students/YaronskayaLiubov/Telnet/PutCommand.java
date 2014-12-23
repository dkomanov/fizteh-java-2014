package ru.fizteh.fivt.students.YaronskayaLiubov.Telnet;

import ru.fizteh.fivt.storage.structured.Storeable;

import java.io.IOException;
import java.text.ParseException;

/**
 * Created by luba_yaronskaya on 19.10.14.
 */
public class PutCommand extends TelnetCommand {
    PutCommand() {
        name = "put";
        numberOfArguments = 3;
    }


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

        Storeable row = null;
        Storeable old = null;
        try {
            row = tableProvider.deserialize(tableProvider.getOpenTable(), args[2]);
            old = tableProvider.getOpenTable().put(args[1], row);
        } catch (ParseException e) {
            throw new CommandRuntimeException(e.getMessage());
        }
        if (old != null) {
            return "overwrite";
        } else {
            return "new";
        }
    }

}
