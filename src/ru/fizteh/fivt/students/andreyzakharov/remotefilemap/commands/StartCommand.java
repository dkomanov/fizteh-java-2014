package ru.fizteh.fivt.students.andreyzakharov.remotefilemap.commands;

import ru.fizteh.fivt.students.andreyzakharov.remotefilemap.CommandInterruptException;
import ru.fizteh.fivt.students.andreyzakharov.remotefilemap.MultiFileTableProvider;

public class StartCommand implements Command {
    @Override
    public String execute(MultiFileTableProvider connector, String... args) throws CommandInterruptException {
        int port;
        if (args.length > 2) {
            throw new CommandInterruptException("not started: too many arguments");
        }
        if (args.length < 2) {
            port = 10001;
        } else {
            try {
                port = Integer.valueOf(args[2]);
            } catch (NumberFormatException e) {
                return "not started: invalid port number";
            }
            if (port < 0 || port > 65536) {
                return "not started: invalid port number";
            }
        }
        if (connector.getStatus() == MultiFileTableProvider.ProviderStatus.SERVER) {
            return "not started: already running";
        }
        if (connector.getStatus() == MultiFileTableProvider.ProviderStatus.CONNECTED) {
            return "not started: already running as a client";
        }

        connector.startServer(port);

        return "started at " + port;
    }

    @Override
    public boolean isLocal() {
        return false;
    }

    @Override
    public String toString() {
        return "start";
    }
}
