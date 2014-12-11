package ru.fizteh.fivt.students.deserg.telnet.server;

import ru.fizteh.fivt.students.deserg.telnet.DbCommandExecuter;

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

        System.out.println("New user: " + socket.getInetAddress().getCanonicalHostName() + ":" + socket.getPort());

        while (true) {

            if (!data.isStarted()) {
                System.out.println("Server has got disabled");
                socket.close();
                break;
            }



            byte[] bytes = new byte[512000];
            int readNum = socket.getInputStream().read(bytes);

            if (readNum == -1) {
                System.out.println("The socket is closed");
                break;
            }

            if (!data.isStarted()) {
                System.out.println("Server has got disabled");
                socket.close();
                break;
            }


            String inputCommand = new String(bytes, 0, readNum);

            System.out.println("Read " + readNum + " bytes from socket: " + socket.getInetAddress().getCanonicalHostName());
            System.out.println(inputCommand);

            String result;
            result = DbCommandExecuter.executeDbCommand(inputCommand, data.getDb());
            System.out.println("The result is: \n" + result + "\n");

            socket.getOutputStream().write(result.getBytes());
            System.out.println("Wrote to socket: " + socket.getInetAddress().getCanonicalHostName());

        }

        return 0;
    }

}
