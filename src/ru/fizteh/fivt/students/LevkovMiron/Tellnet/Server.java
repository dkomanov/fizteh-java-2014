package ru.fizteh.fivt.students.LevkovMiron.Tellnet;

import org.junit.rules.TemporaryFolder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Мирон on 07.12.2014 ru.fizteh.fivt.students.LevkovMiron.Tellnet.
 */
public class Server {
    private ServerSocket serverSocket;
    private Set<Socket> listClients = new HashSet<>();
    final ExecutorService threadPool = Executors.newCachedThreadPool();

    private CTableProvider provider;
    private TemporaryFolder folder;

    public Server(int port) throws IOException {
        folder = new TemporaryFolder();
        folder.create();
        provider = new CTableProvider(folder.newFolder("ServerFolder").toPath());
        serverSocket = new ServerSocket(port);
        threadPool.submit(listenForClients);
    }

    public Server() throws IOException {
        this(10001);
    }

    public void stop() throws IOException {
        threadPool.shutdown();
        serverSocket.close();
        for (Socket s : listClients) {
            s.close();
        }
        threadPool.shutdown();
        System.exit(0);
    }

    public Set<Socket> listUsers() {
        return listClients;
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    private Runnable listenForClients = new Runnable() {
        @Override
        public void run() {
            try {
                while (!serverSocket.isClosed()) {
                    Socket clientSocket = null;
                    try {
                        clientSocket = serverSocket.accept();
                    } catch (SocketException e) {
                        return;
                    }
                    listClients.add(clientSocket);
                    threadPool.submit(new ClientTask(clientSocket));
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            threadPool.submit(listenForClients);
        }
    };

    private class ClientTask implements Runnable {
        private Socket socket;
        private DataInputStream in;
        private DataOutputStream out;
        private CTable currentT;
        private ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
        public ClientTask(Socket clientSocket) throws IOException {
            socket = clientSocket;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        }

        @Override
        public void run() {
            while (!socket.isClosed()) {
                readClient();
            }
        }

        private void readClient() {
            try {
                String input = in.readUTF();
                String[] commands = input.split(";");
                for (String cmd : commands) {
//                    System.out.println("agr : " + cmd);
                    String s = runCommand(cmd.trim());
//                    System.out.println("res : " + s);
                    if (cmd.equals("exit")) {
                        return;
                    }
                    writeClient(s);
                }
            } catch (IOException e) {
                return;
            }
        }

        private void writeClient(String s) {
            try {
                out.writeUTF(s);
                out.flush();
            } catch (IOException e) {
                return;
            }
        }

        private String runCommand(String command) {
            String[] cmd = command.split("\\s(?![^\\(]*\\))(?![^\\[]*\\])");
            StringBuilder builder = new StringBuilder("");
            if (command.equals("show tables")) {
                for (String table : provider.listTables()) {
                    builder.append(table);
                    builder.append(", ");
                }
                return builder.toString();
            }
            if (command.equals("get columns count")) {
                if (currentT == null) {
                    return ("No tables in usage");
                }
                return builder.append(currentT.getColumnsCount()).toString();
            }
            if (command.equals("get name")) {
                if (currentT == null) {
                    return ("No tables in usage");
                }
                return currentT.getName();
            }
            try {
                switch (cmd[0]) {
                    case "exit":
                        if (currentT != null) {
                            currentT.rollback();
                        }
                        socket.close();
                        listClients.remove(socket);
                        return "empty output";
                    case "use":
                        if (currentT != null && currentT.changesNumber() > 0) {
                            return ("Can't use: " + currentT.changesNumber() + " unsaved shanges");
                        }
                        if (provider.getTable(cmd[1]) != null) {
                            currentT = (CTable) provider.getTable(cmd[1]);
                            return ("Using " + cmd[1]);
                        } else {
                            return ("Table doesn't exist");
                        }
                    case "drop":
                        if (provider.getTable(cmd[1]) != null) {
                            if (currentT == provider.getTable(cmd[1])) {
                                currentT = null;
                            }
                            provider.removeTable(cmd[1]);
                        } else {
                            return (cmd[1] + " doesn't exist");
                        }
                        break;
                    case "create":
                        if (cmd.length != 3) {
                            return ("wrong number of argument to Create");
                        }
                        String name = cmd[1];
                        if (provider.getTable(name) != null) {
                            return ("Already exists");
                        }
                        String forSignature = cmd[2].replaceFirst("\\(", "");
                        forSignature = forSignature.trim();
                        forSignature = forSignature.substring(0, forSignature.length() - 1);
                        Utils utils = new Utils();
                        ArrayList<Class<?>> signature = utils.signature(forSignature);
                        try {
                            provider.createTable(name, signature);
                            return ("created");
                        } catch (IOException e) {
                            return ("I can't create table" + " " + e.getMessage());
                        }
                    case "get": {
                        if (currentT == null) {
                            return ("No tables in usage");
                        }
                        if (cmd.length == 4 && cmd[1].equals("column") && cmd[2].equals("type")) {
                            return (currentT.getColumnType(Integer.parseInt(cmd[3]))).toString();
                        }
                        Storeable result = currentT.get(cmd[1]);
                        return (result == null ? "null" : result.toString());
                    }
                    case "put": {
                        if (currentT == null) {
                            return ("No tables in usage");
                        }
                        Storeable result = currentT.put(cmd[1], new Parser().deserialize(currentT, cmd[2]));
                        return (result == null ? "null" : result.toString());
                    }
                    case "list":
                        if (currentT == null) {
                            return ("No tables in usage");
                        }
                        for (String s : currentT.list()) {
                            builder.append(s);
                            builder.append(", ");
                        }
                        return builder.toString();
                    case "remove": {
                        if (currentT == null) {
                            return ("No tables in usage");
                        }
                        Storeable result = currentT.remove(cmd[1]);
                        return (result == null ? "null" : result.toString());
                    }
                    case "size":
                        if (currentT == null) {
                            return ("No tables in usage");
                        }
                        return builder.append(currentT.size()).toString();
                    case "commit":
                        if (currentT == null) {
                            return ("No tables in usage");
                        }
                        return builder.append(currentT.commit()).toString();
                    case "rollback":
                        if (currentT == null) {
                            return ("No tables in usage");
                        }
                        return builder.append(currentT.rollback()).toString();
                    default:
                        return ("Unknown command");
                }
            } catch (IOException e) {
                return ("IOException " + e.getMessage());
            } catch (ParseException e) {
                builder.append("Wrong JSON format.");
                if (currentT != null) {
                    builder.append(" Expected: ");
                    for (int i = 0; i < currentT.getColumnsCount(); i++) {
                        builder.append(currentT.getColumnType(i).getName() + " ");
                    }
                }
                return builder.toString();
            } catch (IndexOutOfBoundsException e) {
                return ("Wrong command format");
            }
            return "empty output";
        }

    }
}
