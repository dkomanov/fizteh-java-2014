package ru.fizteh.fivt.students.andreyzakharov.remotefilemap.commands;

import ru.fizteh.fivt.students.andreyzakharov.remotefilemap.CommandInterruptException;
import ru.fizteh.fivt.students.andreyzakharov.remotefilemap.MultiFileTableProvider;

public class StatusCommand implements Command {
    @Override
    public String execute(MultiFileTableProvider connector, String... args) throws CommandInterruptException {
        switch (connector.getStatus()) {
            case LOCAL:
                return "local";
            case CONNECTED:
                return "connected";
            case SERVER:
                return "running";
            default:
                return null;
        }
    }

    @Override
    public boolean isLocal() {
        return true;
    }

    @Override
    public String toString() {
        return "status";
    }
}
