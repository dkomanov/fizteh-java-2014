package ru.fizteh.fivt.students.deserg.telnet.client;

import ru.fizteh.fivt.students.deserg.telnet.*;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by deserg on 11.12.14.
 */
public class Client implements Program {

    Socket socket;
    private boolean connected = false;
    String localIP = "localhost";


    @Override
    public void work() {

        System.out.println("\nHello, stanger! Welcome to deserg DataBase!\n");

        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNext()) {
            ArrayList<String> list = FileSystem.readCommandLine(scanner);
            System.out.println(execute(list));
        }
    }



    private String execute(ArrayList<String> arguments) {


        String command = arguments.get(0);
        switch (command) {

            case "connect": {
                return commandConnect(arguments);
            }

            case "disconnect": {
                return commandDisconnect(arguments);
            }

            case "whereami": {
                return commandWhereAmI(arguments);
            }

            case "exit": {
                return commandExit(arguments);
            }

            default: {

                return commandOther(String.join(" ", arguments));
            }

        }


    }

    private String commandConnect(ArrayList<String> arguments) {

        if (arguments.size() < 1 || arguments.size() > 3) {
            return "Wrong argument number";
        }

        if (connected) {
            return "not connected: already connected";
        }

        String strHost = localIP;

        if (arguments.size() == 2) {
            strHost = arguments.get(1);
        }
        InetAddress host;

        try {
            host = InetAddress.getByName(strHost);
        } catch (UnknownHostException ex) {
            return "not connected: wrong host";
        }

        int port = 10001;
        if (arguments.size() == 3) {
            String strPort = arguments.get(2);
            try {
                port = Integer.valueOf(strPort);
            } catch (NumberFormatException ex) {
                return "not connected: wrong port";
            }
        }

        try {
            socket = new Socket(host, port);
            connected = true;
            return "connected";
        } catch (IOException ex) {
            return "not connected: " + ex.getMessage();
        }

    }

    private String commandDisconnect(ArrayList<String> arguments) {

        if (arguments.size() != 1) {
            return "Too many arguments (required: 0)";
        }

        if (!connected) {
            return "not connected";
        }

        try {
            socket.close();
            connected = false;
            return "disconnected";
        } catch (IOException ex) {
            return "not disconnected: " + ex.getMessage();
        }

    }

    private String commandWhereAmI(ArrayList<String> arguments) {

        if (arguments.size() != 1) {
            return "Too many arguments (required: 0)";
        }

        if (!connected) {
            return "not connected";
        }

        if (socket.getInetAddress().getCanonicalHostName().equals(localIP)) {
            return "local " + socket.getInetAddress().getCanonicalHostName() + " " + socket.getPort();
        } else {
            return "remote " + socket.getInetAddress().getCanonicalHostName() + " " + socket.getPort();
        }
    }

    private String commandExit(ArrayList<String> arguments) {

        if (arguments.size() != 1) {
            return "Too many arguments (required: 0)";
        }

        if (connected) {
            try {
                socket.close();
            } catch (IOException ex) {
                System.out.println("problems with disconnecting: " + ex.getMessage() + "\n");
            }
        }


        System.out.println("\nGoodbye, stranger!\n");
        System.exit(0);
        return null;
    }


    private String commandOther(String command) {

        if (!connected) {
            return "not connected";
        }

        try {

            if (socket.isClosed()) {
                connected = false;
                return "connection stopped";
            }

            socket.getOutputStream().write(command.getBytes());

            byte[] bytes = new byte[512000];
            int inpNum = socket.getInputStream().read(bytes);

            if (inpNum == -1) {
                connected = false;
                return "connection stopped";
            }

            return new String(bytes, 0, inpNum);

        } catch (IOException ex) {
            connected = false;

            try {
                socket.close();
                connected = false;
                return "connection stopped";
            } catch (IOException ex1) {
                return "connection stopped";
            }
        }

    }


}
