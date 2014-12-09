package ru.fizteh.fivt.students.VasilevKirill.telnet;

import ru.fizteh.fivt.students.VasilevKirill.telnet.Threads.ClientThread;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Threads.ServerConsoleThread;
import ru.fizteh.fivt.students.VasilevKirill.telnet.structures.ShutdownHookThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kirill on 03.12.2014.
 */
public class ServerMain {
    private static volatile List<String> userInformation = new ArrayList<>();
    private static volatile boolean isClosed = false;

    public static void main(String[] args) {
        try {
            String rootDirectory = System.getProperty("fizteh.db.dir");
            if (rootDirectory == null) {
                throw new IOException("Can't find the directory");
            }
            Object monitor = new Object();
            List<Thread> clients = new ArrayList<>();
            ServerConsoleThread serverThread = new ServerConsoleThread(args, monitor, rootDirectory);
            Thread mainThread = new Thread(serverThread);
            mainThread.start();
            synchronized (monitor) {
                monitor.wait();
            }
            Thread hook = new ShutdownHookThread(serverThread.getTableProvider());
            Runtime.getRuntime().addShutdownHook(hook);
            ServerSocket ss = serverThread.getServerSocket();
            if (ss == null) {
                throw new IOException("Server socket wasn't initialized");
            }
            while (!isClosed) {
                Object isClientConnected = new Object();
                ClientThread client = new ClientThread(ss, serverThread.getTableProvider(), isClientConnected);
                Thread clientThread = new Thread(client);
                clients.add(clientThread);
                clientThread.start();
                synchronized (isClientConnected) {
                    isClientConnected.wait();
                }
                if (isClosed) {
                    break;
                }
                userInformation.add(client.getClientInformation());
            }
            for (Thread it : clients) {
                it.join();
            }
            mainThread.join(); //
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static List<String> getUserInformation() {
        return userInformation;
    }

    public static void closeServer() {
        isClosed = true;
    }

    public static boolean isServerClosed() {
        return isClosed;
    }
}

