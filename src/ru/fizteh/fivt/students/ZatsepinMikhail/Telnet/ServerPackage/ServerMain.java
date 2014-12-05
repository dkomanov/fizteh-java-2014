package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage;

import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap.Shell;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.Commands.TelnetCmdListusers;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.Commands.TelnetCmdStart;

public class ServerMain {
    public static void main(String[] args) {
        Server myServer = new Server();
        Shell<Server> myShell = new Shell<>(myServer);
        setUpShell(myShell);
        myShell.interactiveMode();
    }

    private static void setUpShell(Shell<Server> myShell) {
        myShell.addCommand(new TelnetCmdStart());
        //myShell.addCommand(new TelnetCmdStop());
        myShell.addCommand(new TelnetCmdListusers());
    }
}
