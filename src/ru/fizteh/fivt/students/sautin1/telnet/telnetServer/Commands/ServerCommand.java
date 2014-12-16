package ru.fizteh.fivt.students.sautin1.telnet.telnetServer.Commands;

import ru.fizteh.fivt.students.sautin1.telnet.shell.AbstractCommand;
import ru.fizteh.fivt.students.sautin1.telnet.telnetServer.ServerState;

/**
 * Created by sautin1 on 12/16/14.
 */
public abstract class ServerCommand extends AbstractCommand<ServerState> {
    public ServerCommand(String name, int minArgNumber, int maxArgNumber) {
        super(name, minArgNumber, maxArgNumber);
    }
}
