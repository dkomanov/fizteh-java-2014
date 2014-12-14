package ru.fizteh.fivt.students.VasilevKirill.telnet.Threads;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.shelldata.Command;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.shelldata.Shell;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.shelldata.Status;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.telnet.ListUsersCommand;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.telnet.StartCommand;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.telnet.StopCommand;
import ru.fizteh.fivt.students.VasilevKirill.telnet.structures.MyTableProvider;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kirill on 06.12.2014.
 */
public class ServerConsoleThread implements Runnable {
    private String[] args;
    private Object monitor;
    private volatile Status status;
    private volatile TableProvider tableProvider;

    public ServerConsoleThread(String[] args, Object monitor, String directory) throws IOException {
        this.args = args;
        this.monitor = monitor;
        ServerSocket ss = null;
        status = new Status(ss);
        this.tableProvider = new MyTableProvider(directory);
    }

    @Override
    public void run() {
        Map<String, Command> commandMap = new HashMap<>();
        commandMap.put(new StartCommand().toString(), new StartCommand());
        commandMap.put(new StopCommand().toString(), new StopCommand());
        commandMap.put(new ListUsersCommand().toString(), new ListUsersCommand());
        try {
            status.setMonitor(monitor);
            new Shell(commandMap, status).handle(System.in);
            ((MyTableProvider) tableProvider).close();
            System.exit(0);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public ServerSocket getServerSocket() {
        return status.getServerSocket();
    }

    public TableProvider getTableProvider() {
        return tableProvider;
    }
}
