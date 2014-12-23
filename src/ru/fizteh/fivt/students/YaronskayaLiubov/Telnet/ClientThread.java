package ru.fizteh.fivt.students.YaronskayaLiubov.Telnet;


import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by luba_yaronskaya on 20.12.14.
 */
public class ClientThread extends Thread implements Runnable {
    private Socket socket;
    private int port;
    private String host;
    private Shell shell;
    Scanner in;
    PrintStream out;


    public ClientThread(Socket socket, Shell shell) {
        this.shell = shell;
        this.socket = socket;
        host = socket.getInetAddress().toString();
        port = socket.getPort();
        try {
            in = new Scanner(socket.getInputStream());
            out = new PrintStream(socket.getOutputStream());
        } catch (IOException e) {
            close();
        }
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            throw new ClientRuntimeException(e.getMessage());
        }
    }

    @Override
    public void run() {
        while (true) {
            if (in.hasNext()) {
                String input = in.nextLine();
                out.println(shell.executeCommand(input, true));
            }
        }
    }
}
