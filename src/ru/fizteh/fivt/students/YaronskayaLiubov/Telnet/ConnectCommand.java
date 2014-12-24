package ru.fizteh.fivt.students.YaronskayaLiubov.Telnet;

/**
 * Created by luba_yaronskaya on 21.12.14.
 */
public class ConnectCommand extends TelnetCommand {
    public ConnectCommand() {
        name = "connect";
    }

    @Override
    String execute(RemoteDataTableProvider tableProvider, String[] args) {
        if (args.length != 3) {
            throw new CommandRuntimeException(name + ": illegal number of arguments");
        }
        String host = args[1];
        int port = 10001;
        try {
            port = Integer.valueOf(args[2]);
        } catch (NumberFormatException e) {
            throw new ClientRuntimeException("not connected: wrong port number");
        }
        if (port <= 1024 || port >= 65536) {
            throw new ClientRuntimeException("not connected: wrong port number");
        }
        if (tableProvider.isConnected()) {
            throw new ClientRuntimeException("not connected: already connected");
        }
        try {
            tableProvider.connect(host, port);
        } catch (ClientRuntimeException e) {
            throw new ClientRuntimeException("not connected: " + e.getMessage());
        }
        return "connected";
    }
}
