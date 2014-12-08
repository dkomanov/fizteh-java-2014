package ru.fizteh.fivt.students.gudkov394.telnet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {
    private ServerSocket serverSocket;
    private ArrayList<Socket> listClients = new ArrayList<>();
    final ExecutorService threadPool = Executors.newCachedThreadPool();

    private CurrentTable currentT;
    private ParallelTableProvider provider;

    public Server() throws IOException {
        this(10001);
    }

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        threadPool.submit(listenForClients);
        File folder = new File("testServer");
        if (!folder.exists()) {
            folder.mkdir();
        }
        provider = new ParallelTableProvider(folder.toPath());
    }

    public void stop() throws IOException {
        threadPool.shutdown();
        serverSocket.close();
        for (Socket s : listClients) {
            s.close();
        }
        threadPool.shutdown();

    }

    public ArrayList<Socket> listUsers() {
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
                    Socket clientSocket;
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
                    writeClient(runCommand(cmd.trim()));
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
                provider.listTables().forEach(builder::append);
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
                    case "use":
                        if (currentT != null && currentT.changesNumber() > 0) {
                            return ("Can't use: " + currentT.changesNumber() + " unsaved shanges");
                        }
                        if (provider.getTable(cmd[1]) != null) {
                            currentT = (CurrentTable) provider.getTable(cmd[1]);
                            currentT.check();
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
                    case "get":
                        if (currentT == null) {
                            return ("No tables in usage");
                        }
                        if (cmd.length == 4 && cmd[1].equals("column") && cmd[2].equals("type")) {
                            return (currentT.getColumnType(Integer.parseInt(cmd[3]))).toString();
                        }
                        if (currentT.get(cmd[1]) == null) {
                            return null;
                        }
                        return currentT.get(cmd[1]).toString();
                    case "put":
                        if (currentT == null) {
                            return ("No tables in usage");
                        }
                        if (currentT.put(cmd[1], new Parser().deserialize(currentT, cmd[2])) == null) {
                            return null;
                        }
                        return currentT.put(cmd[1], new Parser().deserialize(currentT, cmd[2])).toString();
                    case "list":
                        if (currentT == null) {
                            return ("No tables in usage");
                        }
                        currentT.list().forEach(builder::append);
                        return builder.toString();
                    case "remove":
                        if (currentT == null) {
                            return ("No tables in usage");
                        }
                        if (currentT.remove(cmd[1]) == null) {
                            return null;
                        }
                        return currentT.remove(cmd[1]).toString();
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
                builder.append("Wrong JSON format. Expected: ");
                for (int i = 0; i < currentT.getColumnsCount(); i++) {
                    builder.append(currentT.getColumnType(i).getName()).append(" ");
                }
                return builder.toString();
            } catch (IndexOutOfBoundsException e) {
                return ("Wrong command format");
            }
            return "";
        }

    }
}
