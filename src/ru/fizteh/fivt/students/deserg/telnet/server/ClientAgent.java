package ru.fizteh.fivt.students.deserg.telnet.server;

import ru.fizteh.fivt.students.deserg.telnet.DbCommandExecuter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by deserg on 11.12.14.
 */
public class ClientAgent implements Callable<Integer> {

    Socket socket;
    CommonData data;
    ReadWriteLock lock = new ReentrantReadWriteLock(true);

    public ClientAgent(Socket socket, CommonData data) {

        this.socket = socket;

        lock.readLock().lock();
        this.data = data;
        lock.readLock().unlock();
    }

    @Override
    public Integer call() throws Exception {

        System.out.println("Client agent has started it's work");
        System.out.println("The socket is: " + socket.getInetAddress().getCanonicalHostName() + ":" + socket.getPort());


        while (true) {

            System.out.println("Reading from socket: " + socket.getInetAddress().getCanonicalHostName() + ":" + socket.getPort());

            byte[] bytes = new byte[512000];
            socket.getInputStream().read(bytes);
            String inputCommand = new String(bytes);

            System.out.println("Read from socket: " + socket.getInetAddress().getCanonicalHostName());
            System.out.println(inputCommand);

            if (inputCommand.equals("disconnect") || inputCommand.equals("exit")) {

                data.db.close();
                socket.close();
                System.out.println("Closed socket: " + socket.getInetAddress().getCanonicalHostName());
                break;

            } else {

                System.out.println("Starting to execute");
                String result;
                result = DbCommandExecuter.executeDbCommand(inputCommand, data.db);
                System.out.println("The result is: \n" + result + "\n");

                System.out.println("Writing to socket: " + socket.getInetAddress().getCanonicalHostName());
                socket.getOutputStream().write(result.getBytes());
                System.out.println("Wrote to socket: " + socket.getInetAddress().getCanonicalHostName());
            }

        }

        return 0;
    }

}
