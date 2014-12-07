package ru.fizteh.fivt.students.LevkovMiron.Tellnet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Мирон on 07.12.2014 ru.fizteh.fivt.students.LevkovMiron.Tellnet.
 */
public class Server {
    private ServerSocket serverSocket;
    private ArrayList<Socket> listClients = new ArrayList<>();
    final ExecutorService threadPool = Executors.newCachedThreadPool();

    private CTable currentT;
    private CTableProvider provider;

    public Server() throws IOException {
        serverSocket = new ServerSocket(10001);
        threadPool.submit(listenForClients);
    }

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        threadPool.submit(listenForClients);
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
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            String[] commands = input.split(";");
            for (String cmd : commands) {
                writeClient(runCommand(cmd.trim()));
            }
            readClient();
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
                if (cmd[0].equals("use")) {
                    if (currentT != null && currentT.changesNumber() > 0) {
                        return ("Can't use: " + currentT.changesNumber() + " unsaved shanges");
                    }
                    if (provider.getTable(cmd[1]) != null) {
                        currentT = (CTable) provider.getTable(cmd[1]);
                        return ("Using " + cmd[1]);
                    } else {
                        return ("Table doesn't exist");
                    }
                } else if (cmd[0].equals("drop")) {
                    if (provider.getTable(cmd[1]) != null) {
                        if (currentT == provider.getTable(cmd[1])) {
                            currentT = null;
                        }
                        provider.removeTable(cmd[1]);
                    } else {
                        return (cmd[1] + " doesn't exist");
                    }
                } else if (cmd[0].equals("create")) {
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
                } else if (cmd[0].equals("get")) {
                    if (currentT == null) {
                        return ("No tables in usage");
                    }
                    if (cmd.length == 4 && cmd[1].equals("column") && cmd[2].equals("type")) {
                        return (currentT.getColumnType(Integer.parseInt(cmd[3]))).toString();
                    }
                    return currentT.get(cmd[1]).toString();
                } else if (cmd[0].equals("put")) {
                    if (currentT == null) {
                        return ("No tables in usage");
                    }
                    return currentT.put(cmd[1], new Parser().deserialize(currentT, cmd[2])).toString();
                } else if (cmd[0].equals("list")) {
                    if (currentT == null) {
                        return ("No tables in usage");
                    }
                    for (String s : currentT.list()) {
                        builder.append(s);
                    }
                    return builder.toString();
                } else if (cmd[0].equals("remove")) {
                    if (currentT == null) {
                        return ("No tables in usage");
                    }
                    return currentT.remove(cmd[1]).toString();
                } else if (cmd[0].equals("size")) {
                    if (currentT == null) {
                        return ("No tables in usage");
                    }
                    return builder.append(currentT.size()).toString();
                } else if (cmd[0].equals("commit")) {
                    if (currentT == null) {
                        return ("No tables in usage");
                    }
                    return builder.append(currentT.commit()).toString();
                } else if (cmd[0].equals("rollback")) {
                    if (currentT == null) {
                        return ("No tables in usage");
                    }
                    return builder.append(currentT.rollback()).toString();
                } else {
                    return ("Unknown command");
                }
            } catch (IOException e) {
                return ("IOException " + e.getMessage());
            } catch (ParseException e) {
                builder.append("Wrong JSON format. Expected: ");
                for (int i = 0; i < currentT.getColumnsCount(); i++) {
                    builder.append(currentT.getColumnType(i).getName() + " ");
                }
                return builder.toString();
            } catch (IndexOutOfBoundsException e) {
                return ("Wrong command format");
            }
            return "";
        }

    }
}
