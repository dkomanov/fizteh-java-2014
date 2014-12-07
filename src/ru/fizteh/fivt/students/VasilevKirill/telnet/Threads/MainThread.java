package ru.fizteh.fivt.students.VasilevKirill.telnet.Threads;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.shelldata.Status;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.telnet.StartCommand;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Commands.telnet.StopCommand;
import ru.fizteh.fivt.students.VasilevKirill.telnet.structures.MyTableProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;

/**
 * Created by Kirill on 06.12.2014.
 */
public class MainThread implements Runnable {
    private String[] args;
    private Object monitor;
    private volatile Status status;
    private volatile TableProvider tableProvider;

    public MainThread(String[] args, Object monitor, String directory) throws IOException {
        this.args = args;
        this.monitor = monitor;
        ServerSocket ss = null;
        status = new Status(ss);
        this.tableProvider = new MyTableProvider(directory);
    }

    /*@Override
    public void run() {
        try {
            Map<String, Command> commands = new HashMap<String, Command>();
            commands.put(new StartCommand().toString(), new StartCommand());
            commands.put(new StopCommand().toString(), new StopCommand());
            try {
                int retValue = 0;
                String rootDirectory = System.getProperty("fizteh.db.dir");
                if (rootDirectory == null) {
                    throw new IOException("Can't find the directory");
                }
                TableProvider dataBase = new MyTableProviderFactory().create(rootDirectory);
                Status status = new Status(dataBase);
                if (args.length == 0) {
                    new Shell(commands, status, monitor).handle(System.in);
                } else {
                    retValue = new Shell(commands, status, monitor).handle(args);
                }
                System.exit(retValue);
            } catch (IOException e) {
                System.out.println(e.getMessage());
                System.exit(-1);
            }
        } catch (Exception e) {
            if (e.getMessage().equals("")) {
                System.out.println(e);
            } else {
                System.out.println(e.getMessage());
            }
            System.exit(-1);
        }
    }*/

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
                            monitor.notifyAll();
                        }
                        break;
                    case "stop":
                        new StopCommand().execute(args, status);
                        endOfCycle = true;
                        break;
                    default:
                        continue;
                }
            }
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
