package ru.fizteh.fivt.students.andreyzakharov.remotefilemap.commands;

import ru.fizteh.fivt.students.andreyzakharov.remotefilemap.CommandInterruptException;
import ru.fizteh.fivt.students.andreyzakharov.remotefilemap.ConnectionInterruptException;
import ru.fizteh.fivt.students.andreyzakharov.remotefilemap.MultiFileTableProvider;

public class ExitCommand implements Command {
    @Override
    public String execute(MultiFileTableProvider connector, String... args) throws CommandInterruptException {
        if (connector.getCurrent() != null && connector.getCurrent().getNumberOfUncommittedChanges() > 0) {
            return connector.getCurrent().getNumberOfUncommittedChanges() + " unsaved changes";
        }
        if (connector.getStatus() == MultiFileTableProvider.ProviderStatus.CONNECTED) {
            try {
                connector.disconnect();
            } catch (ConnectionInterruptException e) {
                //
            }
            return "disconnected";
        } else {
            connector.close();
            System.exit(0);
            return null; // silly Java
        }
    }

    @Override
    public boolean isLocal() {
        return true;
    }

    @Override
    public String toString() {
        return "exit";
    }
}
