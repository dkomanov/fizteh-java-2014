package ru.fizteh.fivt.students.YaronskayaLiubov.Telnet;

/**
 * Created by luba_yaronskaya on 21.12.14.
 */
public class DisconnectCommand extends TelnetCommand {
    public DisconnectCommand() {
        name = "disconnect";
    }

    @Override
    String execute(RemoteDataTableProvider tableProvider, String[] args) {
        if (args.length != 1) {
            throw new CommandRuntimeException(name + ": illegal number of arguments");
        }

        if (!tableProvider.isConnected()) {
            return "not connected";
        }
        try {
            tableProvider.disconnect();
        } catch (ClientRuntimeException e) {
            throw new ClientRuntimeException("disconnect: " + e.getMessage());
        }
        return "disconnected";
    }
}
