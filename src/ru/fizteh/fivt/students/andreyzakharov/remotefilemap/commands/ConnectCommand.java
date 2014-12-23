package ru.fizteh.fivt.students.andreyzakharov.remotefilemap.commands;

import ru.fizteh.fivt.students.andreyzakharov.remotefilemap.CommandInterruptException;
import ru.fizteh.fivt.students.andreyzakharov.remotefilemap.ConnectionInterruptException;
import ru.fizteh.fivt.students.andreyzakharov.remotefilemap.MultiFileTableProvider;

public class ConnectCommand implements Command {
    @Override
    public String execute(MultiFileTableProvider connector, String... args) throws CommandInterruptException {
        if (args.length > 3) {
            throw new CommandInterruptException("not connected: too many arguments");
        }
        if (connector.getStatus() == MultiFileTableProvider.ProviderStatus.SERVER) {
            return "not connected: already running as a server";
        }
        if (connector.getStatus() == MultiFileTableProvider.ProviderStatus.CONNECTED) {
            return "not connected: already connected";
        }
        int port = 10001;
        if (args.length == 3) {
            try {
                port = Integer.valueOf(args[2]);
            } catch (NumberFormatException e) {
                throw new CommandInterruptException("not connected: invalid port number");
            }
            if (port < 0 || port > 65536) {
                return "not connected: invalid port number";
            }
        }
        String host = "localhost";
        if (args.length >= 2) {
            host = args[1];
        }
        try {
            connector.connect(host, port);
        } catch (ConnectionInterruptException e) {
            return "not connected: " + e.getMessage();
        }
        return "connected";
    }

    @Override
    public boolean isLocal() {
        return true;
    }

    @Override
    public String toString() {
        return "connect";
    }
}
