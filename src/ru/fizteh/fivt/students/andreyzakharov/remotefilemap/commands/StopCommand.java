package ru.fizteh.fivt.students.andreyzakharov.remotefilemap.commands;

import ru.fizteh.fivt.students.andreyzakharov.remotefilemap.CommandInterruptException;
import ru.fizteh.fivt.students.andreyzakharov.remotefilemap.MultiFileTableProvider;

public class StopCommand implements Command {
    @Override
    public String execute(MultiFileTableProvider connector, String... args) throws CommandInterruptException {
        return null;
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
