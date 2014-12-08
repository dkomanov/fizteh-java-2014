package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.Commands;

import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.Server;

import java.io.IOException;

public class TelnetCmdStop extends TelnetCommand {
    public TelnetCmdStop() {
        name = "stop";
        numberOfArguments = 1;
    }

    @Override
    public boolean run(Server myServer, String[] args) {
        if (!myServer.isStarted()) {
            System.err.println("not started");
            return false;
        }
        try {
            myServer.stopServer();
            System.err.println("stopped at port " + myServer.getActivePort());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return true;
    }
}
