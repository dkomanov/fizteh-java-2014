package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.CommandsServer;

import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.Server;

public class TelnetCmdStop extends TelnetCommand {
    public TelnetCmdStop() {
        name = "stop";
        numberOfArguments = 1;
    }

    @Override
    public boolean run(Server myServer, String[] args) {
        if (!myServer.isStarted()) {
            System.out.println("not started");
            return false;
        }
        myServer.stopServer();
        System.out.println("stopped at port " + myServer.getActivePort());
        return true;
    }
}
