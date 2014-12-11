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


        try {
            ServerSocket serverSocket = new ServerSocket(data.port);
            ExecutorService service = Executors.newFixedThreadPool(5);

            while (true) {

                Socket socket = serverSocket.accept();
                data.users.add(socket.getInetAddress().toString());
                Future<Integer> future = service.submit(new ClientAgent(socket, data));

            }

        } catch (IOException ex) {
            System.out.println("Error with socket");
            System.exit(1);
        }

        return 0;
    }

}
