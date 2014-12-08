package ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.remote;

import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.Shell;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by akhtyamovpavel on 08.12.14.
 */
public class ServerResponder extends Thread {
    Socket socket;
    Shell shell;
    Scanner stream;
    PrintStream outStream;

    public boolean isServerOk() {
        return serverOk;
    }

    volatile boolean serverOk;

    ServerResponder(Shell shell, Socket socket) {
        this.shell = shell;
        this.socket = socket;
        serverOk = true;
        try {
            stream = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            shutdown();
        }

    }

    public void shutdown() {
        serverOk = false;
        try {
            socket.close();
        } catch (IOException e) {
            //
        }
    }



    @Override
    public void run() {
        while (serverOk) {
            if (stream.hasNext()) {
                String inputString = stream.next();

                ArrayList<String> result = shell.processInteractiveRequest(inputString, true);
                for (String string : result) {
                    outStream.println(string);
                }
            }
        }
        shutdown();
    }
}
