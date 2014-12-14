package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap.Shell;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.CommandsServer.TelnetCmdListUsers;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.CommandsServer.TelnetCmdStart;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.CommandsServer.TelnetCmdStop;

import java.io.IOException;

public class ServerMain {
    public static void main(String[] args) {
        String baseDir = System.getProperty("fizteh.db.dir");
        TableProvider dataBase;
        try {
            dataBase = new MFileHashMap(baseDir);
            Server myServer = new Server(dataBase);
            Runtime.getRuntime().addShutdownHook(new Thread(() -> myServer.stopServer()));
            Shell<Server> myShell = new Shell<>(myServer);
            setUpShell(myShell);
            myShell.interactiveMode();
            myServer.stopServer();
        } catch (IOException e) {
            System.out.println("incorrect directory");
            System.exit(2);
        }
    }

    private static void setUpShell(Shell<Server> myShell) {
        myShell.addCommand(new TelnetCmdStart());
        myShell.addCommand(new TelnetCmdStop());
        myShell.addCommand(new TelnetCmdListUsers());
    }
}
