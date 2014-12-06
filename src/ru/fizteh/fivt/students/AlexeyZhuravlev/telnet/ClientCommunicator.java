package ru.fizteh.fivt.students.AlexeyZhuravlev.telnet;

import java.net.Socket;

/**
 * @author AlexeyZhuravlev
 */
public class ClientCommunicator extends Thread {

    Socket socket;

    ClientCommunicator(Socket passedSocket) {
        socket = passedSocket;
    }

    @Override
    public void run() {

    }
}
