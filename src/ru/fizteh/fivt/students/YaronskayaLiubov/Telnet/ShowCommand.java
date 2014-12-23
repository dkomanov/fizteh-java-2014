package ru.fizteh.fivt.students.YaronskayaLiubov.Telnet;

import java.io.IOException;
import java.util.Map;

/**
 * Created by luba_yaronskaya on 18.10.14.
 */
public class ShowCommand extends TelnetCommand {
    ShowCommand() {
        name = "show";
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

        if (!args[1].equals("tables")) {
            throw new CommandRuntimeException("incorrect argument");
        }
        Map<String, StoreableDataTable> tables = tableProvider.getTables();
        if (tables == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (StoreableDataTable table : tables.values()) {
            sb.append(table.getName()).append(' ').append(table.size()).append('\n');
        }
        return sb.toString();
    }
}
