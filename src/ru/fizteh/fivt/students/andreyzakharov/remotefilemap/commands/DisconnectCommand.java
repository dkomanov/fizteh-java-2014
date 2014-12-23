package ru.fizteh.fivt.students.andreyzakharov.remotefilemap.commands;

import ru.fizteh.fivt.students.andreyzakharov.remotefilemap.CommandInterruptException;
import ru.fizteh.fivt.students.andreyzakharov.remotefilemap.ConnectionInterruptException;
import ru.fizteh.fivt.students.andreyzakharov.remotefilemap.MultiFileTableProvider;

public class DisconnectCommand implements Command {
    @Override
    public String execute(MultiFileTableProvider connector, String... args) throws CommandInterruptException {
        if (connector.getStatus() == MultiFileTableProvider.ProviderStatus.CONNECTED) {
            try {
                connector.disconnect();
                return "disconnected";
            } catch (ConnectionInterruptException e) {
                return "disconnect: " + e.getMessage();
            }
        }
        return "not connected";
    }

    @Override
    public boolean isLocal() {
        return true;
    }

    @Override
    public String toString() {
        return "disconnect";
    }
}
