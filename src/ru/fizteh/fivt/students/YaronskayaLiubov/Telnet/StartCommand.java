package ru.fizteh.fivt.students.YaronskayaLiubov.Telnet;

import java.io.IOException;

/**
 * Created by luba_yaronskaya on 19.12.14.
 */
public class StartCommand extends TelnetCommand {
    public StartCommand() {
        name = "start";
    }

    @Override
    String execute(RemoteDataTableProvider tableProvider, String[] args) {
        int port = 10001;
        if (args.length > 2) {
            throw new CommandRuntimeException(name + ": illegal number of arguments");
        }
        if (args.length > 1) {
            try {
                port = Integer.valueOf(args[1]);
            } catch (NumberFormatException e) {
                throw new ServerRuntimeException("not started: wrong port number");
            }
            if (port <= 1024 || port >= 65536) {
                throw new ServerRuntimeException("not started: wrong port number");
            }
        }
        if (tableProvider.isServer()) {
            throw new ServerRuntimeException("not started: already started");
        }

        try {
            tableProvider.start(port);
        } catch (IOException e) {
            throw new CommandRuntimeException(e.getMessage());
        }

        return "started at " + port;
    }
}
