package ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;
import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.FileMap.Shell;
import ru.fizteh.fivt.students.ZatsepinMikhail.Proxy.MultiFileHashMap.MFileHashMapFactory;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.Commands.TelnetCmdListUsers;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.Commands.TelnetCmdStart;
import ru.fizteh.fivt.students.ZatsepinMikhail.Telnet.ServerPackage.Commands.TelnetCmdStop;

import java.io.IOException;

public class ServerMain {
    public static void main(String[] args) {
        String baseDir = System.getProperty("fizteh.db.dir");
        TableProviderFactory dataBaseFactory = new MFileHashMapFactory();
        TableProvider dataBase;
        try {
            dataBase = dataBaseFactory.create(baseDir);
            Server myServer = new Server(dataBase);
            Shell<Server> myShell = new Shell<>(myServer);
            setUpShell(myShell);
            myShell.interactiveMode();
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
