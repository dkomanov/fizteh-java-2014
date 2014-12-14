package ru.fizteh.fivt.students.andreyzakharov.remotefilemap.commands;

import ru.fizteh.fivt.students.andreyzakharov.remotefilemap.CommandInterruptException;
import ru.fizteh.fivt.students.andreyzakharov.remotefilemap.MultiFileTableProvider;

import java.io.IOException;

public class StopCommand implements Command {
    @Override
    public String execute(MultiFileTableProvider connector, String... args) throws CommandInterruptException {
        try {
            if (connector.getStatus() == MultiFileTableProvider.ProviderStatus.SERVER) {
                int port = connector.stop();
                return "stopped at " + port;
            } else {
                return "not started";
            }
        } catch (IOException e) {
            throw new CommandInterruptException("stop: " + e.getMessage());
        }
    }

    @Override
    public boolean isLocal() {
        return false;
    }

    @Override
    public String toString() {
        return "stop";
    }
}
