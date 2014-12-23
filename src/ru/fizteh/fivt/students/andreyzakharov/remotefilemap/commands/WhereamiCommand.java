package ru.fizteh.fivt.students.andreyzakharov.remotefilemap.commands;

import ru.fizteh.fivt.students.andreyzakharov.remotefilemap.CommandInterruptException;
import ru.fizteh.fivt.students.andreyzakharov.remotefilemap.MultiFileTableProvider;

public class WhereamiCommand implements Command {
    @Override
    public String execute(MultiFileTableProvider connector, String... args) throws CommandInterruptException {
        if (connector.getStatus() == MultiFileTableProvider.ProviderStatus.CONNECTED) {
            return "remote " + connector.getHost() + " " + connector.getPort();
        } else {
            return "local";
        }
    }

    @Override
    public boolean isLocal() {
        return true;
    }

    @Override
    public String toString() {
        return "whereami";
    }
}
