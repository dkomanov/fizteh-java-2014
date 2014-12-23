package ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.remote;

import ru.fizteh.fivt.students.akhtyamovpavel.remotedatabase.Shell;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by akhtyamovpavel on 08.12.14.
 */
public class ServerResponder extends Thread {
    public Socket getSocket() {
        return socket;
    }

    Socket socket;
    Shell shell;
    Scanner stream;
    PrintStream outStream;


    ServerResponder(Shell shell, Socket socket) {
        this.shell = shell;
        this.socket = socket;
        try {
            stream = new Scanner(socket.getInputStream());
            outStream = new PrintStream(socket.getOutputStream());
        } catch (IOException e) {
            shutdown();
        }
    }

    public void shutdown() {
        try {
            socket.close();
        } catch (IOException e) {
            //
        }
    }



    @Override
    public void run() {
        while (true) {
            if (stream.hasNext()) {
                String inputString = stream.nextLine();

                ArrayList<String> result = shell.processInteractiveRequest(inputString, true);
                for (String string : result) {
                    outStream.println(string);
                }
            }
            if (!socket.isConnected() || socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    break;
                }
            }

        }
        shutdown();
    }
}
