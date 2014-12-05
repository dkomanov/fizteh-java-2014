package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.Commands;

import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.Server;

import java.net.SocketAddress;
import java.util.List;

public class TelnetCmdListusers extends TelnetCommand {
    public TelnetCmdListusers() {
        name = "listusers";
        numberOfArguments = 1;
    }

    @Override
    public boolean run(Server myServer, String[] args) {
        if (!myServer.isStarted()) {
            System.out.println("not started");
            return false;
        }
        List<SocketAddress> clients = myServer.listUsers();

        for (SocketAddress oneClient : clients) {
            System.out.println(oneClient);
        }
        return true;
    }
}
