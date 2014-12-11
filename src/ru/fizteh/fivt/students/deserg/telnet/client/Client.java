package ru.fizteh.fivt.students.deserg.telnet.client;

import ru.fizteh.fivt.students.deserg.telnet.Program;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by deserg on 11.12.14.
 */
public class Client implements Program {

    Socket socket;
    DataInputStream inputStream;
    DataOutputStream outputStream;
    private boolean connected = false;

    public Client() {


    }

    @Override
    public void work() {

        while (true) {

            System.out.print("$ ");
            String lineStr = "";

            Scanner lineScan = new Scanner(System.in);
            if (lineScan.hasNext()) {
                lineStr = lineScan.nextLine();
            } else {
                System.exit(1);
            }

            String[] argumentAr = lineStr.split("\\s+");
            ArrayList<String> arguments = new ArrayList<>();

            Collections.addAll(arguments, argumentAr);
            System.out.println(execute(arguments, lineStr));

        }

    }



    private String execute(ArrayList<String> arguments, String notDevided) {


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

            default: {
                return commandOther(notDevided);
            }

        }


    }

    private String commandConnect(ArrayList<String> arguments) {

        if (arguments.size() < 2 || arguments.size() > 3) {
            return "Wrong argument number";
        }

        if (connected) {
            return "not connected: already connected";
        }

        String strHost = arguments.get(1);
        InetAddress host;

        try {
            host = InetAddress.getByName(strHost);
        } catch (UnknownHostException ex) {
            return "not connected: wrong host";
        }

        int port;
        if (arguments.size() == 2) {
            port = 10001;
        } else {
            String strPort = arguments.get(2);
            try {
                port = Integer.valueOf(strPort);
            } catch (NumberFormatException ex) {
                return "not connected: wrong port";
            }
        }

        try {
            socket = new Socket(host, port);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
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
            return "disconnected";
        } catch (IOException ex) {
            return "not disconnected: " + ex.getMessage();
        }

    }

    private String commandWhereAmI(ArrayList<String> arguments) {

        if (arguments.size() != 1) {
            return "Too many arguments (required: 0)";
        }

        String mode = System.getProperty("mode");

        if (mode != null) {
            return "local";
        }

        if (connected) {
            return "remote " + socket.getInetAddress() + socket.getPort();
        } else {
            return "remote";
        }

    }



    private String commandOther(String command) {

        if (!connected) {
            return "not connected";
        }

        try {
            outputStream.writeChars(command);
            return inputStream.readUTF();

        } catch (IOException ex) {
            return "IO exception";
        }


    }


}
