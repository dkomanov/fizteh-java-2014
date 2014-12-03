package ru.fizteh.fivt.students.irina_karatsapova.parallel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClentMain {
    public static void main(String[] args) {
        try {
            start(args);
        } catch (IOException e) {
            System.err.println("Something went wrong with the server or client");
            System.exit(-1);
        }
    }

    public static void start(String[] args) throws IOException {

        System.out.println("Welcome to Client side");

        String connectingIp = "127.0.0.1";
        if (args.length != 0) {
            connectingIp = args[0];
        }

        System.out.println("Connecting to... " + connectingIp);

        Socket fromserver;
        try {
            fromserver = new Socket(connectingIp, 4444);
        } catch (IOException e) {
            System.out.println("Server is not listening");
            return;
        }

        BufferedReader fromServerStream  = new BufferedReader(new InputStreamReader(fromserver.getInputStream()));
        PrintWriter toServerStream = new PrintWriter(fromserver.getOutputStream(), true);
        BufferedReader fromUserStream = new BufferedReader(new InputStreamReader(System.in));

        String fuser;
        String fserver;

        fserver = fromServerStream.readLine();
        System.out.print(fserver);
        while ((fuser = fromUserStream.readLine()) != null) {
            toServerStream.println(fuser);
            if (fuser.equalsIgnoreCase("exit")) {
                break;
            }
            fserver = fromServerStream.readLine();
            while (!fserver.startsWith("$")) {
                System.out.println(fserver);
                fserver = fromServerStream.readLine();
            }
            System.out.print("$ ");
        }
        toServerStream.close();
        fromServerStream.close();
        fromUserStream.close();
        fromserver.close();
    }
}
