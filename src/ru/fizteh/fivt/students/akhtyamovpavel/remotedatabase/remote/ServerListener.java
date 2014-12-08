package ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.remote;

import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.Shell;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by akhtyamovpavel on 08.12.14.
 */
public class ServerListener extends Thread {
    ServerSocket serverSocket;
    Shell shell;

    public ArrayList<ServerResponder> getResponders() {
        return responders;
    }

    ArrayList<ServerResponder> responders;

    public ServerListener(Shell shell, ServerSocket serverSocket) {
        this.shell = shell;
        this.serverSocket = serverSocket;
        responders = new ArrayList<>();
    }

    @Override
    public void run() {
        boolean finished = false;
        while(!finished) {
            try {
                Socket acceptedSocket = serverSocket.accept();
                ServerResponder responder = new ServerResponder(shell, acceptedSocket);
                responder.start();
                responders.add(responder);
            } catch (IOException e) {
                finished = true;
                for (ServerResponder responder: responders) {
                    responder.shutdown();
                }
            }
        }
    }
}
