package ru.fizteh.fivt.students.VasilevKirill.telnet.Threads;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.shelldata.Status;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.telnet.StartCommand;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.telnet.StopCommand;
import ru.fizteh.fivt.students.VasilevKirill.telnet.ServerMain;
import ru.fizteh.fivt.students.VasilevKirill.telnet.structures.MyTableProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.List;

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
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String command = "";
            boolean endOfCycle = false;
            while (!endOfCycle) {
                System.out.print("$ ");
                command = reader.readLine();
                String[] args = command.split("\\s+");
                switch (args[0]) {
                    case "start":
                        new StartCommand().execute(args, status);
                        synchronized (monitor) {
                            ServerMain.startServer();
                            monitor.notifyAll();
                        }
                        break;
                    case "stop":
                        new StopCommand().execute(args, status);
                        endOfCycle = true;
                        ServerMain.closeServer();
                        break;
                    case "list":
                        if (!args[1].equals("users")) {
                            continue;
                        }
                        List<String> users = ServerMain.getUserInformation();
                        for (String it: users) {
                            System.out.println(it);
                        }
                        break;
                    default:
                        continue;
                }
            }
            ((MyTableProvider) tableProvider).close();
            System.exit(0);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public ServerSocket getServerSocket() {
        return status.getServerSocket();
    }

    public TableProvider getTableProvider() {
        return tableProvider;
    }
}
