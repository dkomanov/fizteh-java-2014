package ru.fizteh.fivt.students.deserg.telnet.server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by deserg on 11.12.14.
 */
public class ConnectionManager implements Callable<Integer> {


    CommonData data;
    ReadWriteLock lock = new ReentrantReadWriteLock(true);

    public ConnectionManager(CommonData data) {
        this.data = data;

    }

    @Override
    public Integer call() {

        System.out.println("Connection manager has started his work!");

        try {

            ServerSocket serverSocket;
            ExecutorService service;

            try {
                lock.readLock().lock();
                System.out.println("Port: " + data.getPort());
                serverSocket = new ServerSocket(data.getPort());
                service = Executors.newFixedThreadPool(5);
                
            } finally {
                lock.readLock().unlock();

            }

            while (true) {

                System.out.println("Waiting for the connection...");
                Socket socket = serverSocket.accept();

                try {
                    lock.writeLock().lock();
                    Set<String> users = data.getUsers();
                    users.add(socket.getInetAddress().toString());
                } finally {
                    lock.writeLock().unlock();
                }

                System.out.println("New client connected: " + socket.getInetAddress().getCanonicalHostName()
                        + ":" + socket.getPort());

                service.submit(new ClientAgent(socket, data));
            }


        } catch (IOException ex) {
            System.out.println("Error with socket: " + ex.getMessage());
            System.exit(1);
        }

        return 0;
    }

}
