package ru.fizteh.fivt.students.deserg.telnet.server;

import ru.fizteh.fivt.storage.structured.ColumnFormatException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by deserg on 11.12.14.
 */
public class ConnectionManager implements Callable<Integer> {


    CommonData data;

    public ConnectionManager(CommonData data) {
        this.data = data;

    }

    @Override
    public Integer call() {

        System.out.println("Connection manager has started his work!");

        try {

            System.out.println("Port: " + data.port);
            ServerSocket serverSocket = new ServerSocket(data.port);
            ExecutorService service = Executors.newFixedThreadPool(5);


            while (data.started) {

                System.out.println("Waiting for the connection...");
                Socket socket = serverSocket.accept();
                data.users.add(socket.getInetAddress().getCanonicalHostName());
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
