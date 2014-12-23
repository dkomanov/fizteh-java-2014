package ru.fizteh.fivt.students.YaronskayaLiubov.Telnet;

/**
 * Created by luba_yaronskaya on 21.12.14.
 */
public class ListusersCommand extends TelnetCommand {
    public ListusersCommand() {
        name = "listusers";
    }

    @Override
    String execute(RemoteDataTableProvider tableProvider, String[] args) {

        if (args.length != 1) {
            throw new IllegalArgumentException(name + ": illegal number of arguments");
        }
        if (tableProvider.isLocal()) {
            throw new ServerRuntimeException("not started");
        }
        if (!tableProvider.isServer()) {
            return "";
        }
        return String.join("\n", tableProvider.getClient());
    }
}
