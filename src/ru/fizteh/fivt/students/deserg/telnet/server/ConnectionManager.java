package ru.fizteh.fivt.students.deserg.telnet.server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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

            lock.readLock().lock();
            System.out.println("Port: " + data.getPort());
            ServerSocket serverSocket = new ServerSocket(data.getPort());
            ExecutorService service = Executors.newFixedThreadPool(5);
            lock.readLock().unlock();

            while (true) {

                System.out.println("Waiting for the connection...");
                Socket socket = serverSocket.accept();

                lock.writeLock().lock();
                Set<String> users = data.getUsers();
                users.add(socket.getInetAddress().toString());
                lock.writeLock().unlock();

                System.out.println("New client connected: " + socket.getInetAddress().getCanonicalHostName() + ":" + socket.getPort());

                Future<Integer> future = service.submit(new ClientAgent(socket, data));
            }


        } catch (IOException ex) {
            System.out.println("Error with socket: " + ex.getMessage());
            System.exit(1);
        }

        return 0;
    }

}
