package ru.fizteh.fivt.students.irina_karatsapova.parallel;

import ru.fizteh.fivt.students.irina_karatsapova.parallel.interfaces.TableProvider;
import ru.fizteh.fivt.students.irina_karatsapova.parallel.interfaces.TableProviderFactory;
import ru.fizteh.fivt.students.irina_karatsapova.parallel.table_provider_factory.MyTableProviderFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static String mainDir = "fizteh.db.dir";
    TableProviderFactory tableProviderFactory;
    TableProvider tableProvider;

    public Server() {
        System.setProperty(mainDir, "D:/tmp/db7-proxy");

        tableProviderFactory = new MyTableProviderFactory();
        if (System.getProperty(ServerMain.mainDir) == null) {
            System.err.println("Path to the database is not set up. Use -D" + ServerMain.mainDir + "=...");
            System.exit(1);
        }
        tableProvider = tableProviderFactory.create(System.getProperty(ServerMain.mainDir));
    }

    public void startWork() throws IOException {
        ServerSocket servers = null;

        try {
            servers = new ServerSocket(4444);
        } catch (IOException e) {
            System.err.println("Couldn't listen to port 4444");
            System.exit(-1);
        }

        try {
            while (true) {
                Socket clientSocket = servers.accept();
                System.out.println("Client connected.");
                Thread connectionThread = new EstablishConnectionThread(clientSocket);
                connectionThread.start();
            }
        } catch (IOException e) {
            System.err.println("Can't accept");
            System.exit(-1);
        }

        servers.close();
    }

    class EstablishConnectionThread extends Thread {
        private Socket clientSocket;

        public EstablishConnectionThread(Socket socket) {
            clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader in  = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                Interpreter interpreter = new Interpreter(in, out);
                InterpreterState state = new InterpreterState(tableProvider, out);
                interpreter.interactiveMode(state);
                out.close();
                in.close();
                clientSocket.close();
                System.out.println("Client disconnected.");
            } catch (Exception e) {
                System.out.println("Exception in thread");
            }
        }
    }
}
