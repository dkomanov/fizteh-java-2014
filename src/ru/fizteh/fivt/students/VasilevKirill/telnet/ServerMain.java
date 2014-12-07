package ru.fizteh.fivt.students.VasilevKirill.telnet;

import ru.fizteh.fivt.students.VasilevKirill.telnet.Threads.ClientThread;
import ru.fizteh.fivt.students.VasilevKirill.telnet.Threads.MainThread;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kirill on 03.12.2014.
 */
public class ServerMain {
    public static void main(String[] args) {
        try {
            String rootDirectory = System.getProperty("fizteh.db.dir");
            if (rootDirectory == null) {
                throw new IOException("Can't find the directory");
            }
            Object monitor = new Object();
            List<Thread> clients = new ArrayList<>();
            MainThread serverThread = new MainThread(args, monitor, rootDirectory);
            Thread mainThread = new Thread(serverThread);
            mainThread.start();
            synchronized (monitor) {
                monitor.wait();
            }
            ServerSocket ss = serverThread.getServerSocket();
            if (ss == null) {
                throw new IOException("Server socket wasn't initialized");
            }
            for (int i = 0; i < 1; ++i) {
                Thread clientThread = new Thread(new ClientThread(ss, serverThread.getTableProvider()));
                clients.add(clientThread);
                clientThread.start();
            }
            for (Thread it : clients) {
                it.join();
            }
            mainThread.join();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

