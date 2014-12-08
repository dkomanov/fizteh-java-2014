package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.CommandsServer;

import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.Server;

import java.util.List;

public class TelnetCmdListUsers extends TelnetCommand {
    public TelnetCmdListUsers() {
        name = "listusers";
        numberOfArguments = 1;
    }

    @Override
    public boolean run(Server myServer, String[] args) {
        if (!myServer.isStarted()) {
            System.out.println("not started");
            return false;
        }
        List<String> userList = myServer.getUserList();
        for (String oneUser : userList) {
            System.out.println(oneUser);
        }
        return true;
    }
}
