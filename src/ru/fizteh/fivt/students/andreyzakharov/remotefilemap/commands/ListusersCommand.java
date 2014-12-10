package ru.fizteh.fivt.students.andreyzakharov.remotefilemap.commands;

import ru.fizteh.fivt.students.andreyzakharov.remotefilemap.CommandInterruptException;
import ru.fizteh.fivt.students.andreyzakharov.remotefilemap.MultiFileTableProvider;

public class ListusersCommand implements Command {
    @Override
    public String execute(MultiFileTableProvider connector, String... args) throws CommandInterruptException {
        if (connector.getUsers() != null && connector.getUsers().size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (MultiFileTableProvider.Host host : connector.getUsers()) {
                sb.append(host.host).append(':').append(host.port).append('\n');
            }
            return sb.substring(0, sb.length() - 1);
        } else {
            return "";
        }
    }

    @Override
    public boolean isLocal() {
        return false;
    }

    @Override
    public String toString() {
        return "listusers";
    }
}
