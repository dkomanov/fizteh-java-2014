package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.CommandsServer;

import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.Server;

public class TelnetCmdStart extends TelnetCommand {
    public TelnetCmdStart() {
        name = "start";
        numberOfArguments = -1;
    }

    @Override
    public boolean run(Server myServer, String[] args) {
        if (args.length != 1 & args.length != 2) {
            System.out.println(name + ": wrong number of arguments");
            return false;
        }
        int port = 10001;
        if (args.length == 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println(name + ": wrong argument \"" + args[1] + "\"");
                return false;
            }
        }
        if (myServer.isStarted()) {
            System.out.println("not started: already started");
            return false;
        }
        if (myServer.startServer(port)) {
            System.out.println("started at port " + port);
            return true;
        } else {
            System.out.println("not started: wrong port number");
            return false;
        }
    }
}
