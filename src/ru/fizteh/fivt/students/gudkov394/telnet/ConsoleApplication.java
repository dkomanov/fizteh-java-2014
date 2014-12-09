package ru.fizteh.fivt.students.gudkov394.telnet;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Scanner;

public class ConsoleApplication {


    private int applicationType;

    private CurrentTable currentTable;

    private ParallelTableProvider provider;

    private Server server;
    private Client client;

    public void run() {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        String[] commands = input.split(";");
        for (String cmd : commands) {
            runCommand(cmd.trim());
        }
        run();
    }

    public ConsoleApplication() {
        File f = new File(System.getProperty("fizteh.db.dir"));
        f.mkdir();
        provider = (ParallelTableProvider) new ParallelTableProviderFactory().create(f.getAbsolutePath());
        applicationType = 0;
    }

    public void runCommand(String command) {
        String[] cmd = command.split("\\s(?![^\\(]*\\))(?![^\\[]*\\])");
        if ("start".equals(cmd[0])) {
            if (applicationType == 1) {
                System.out.println("Already started");
                return;
            }
            if (applicationType == 0) {
                if (cmd.length > 2) {
                    System.out.println("wrong command format");
                    return;
                }
                try {
                    int port = 10001;
                    if (cmd.length == 2) {
                        port = Integer.valueOf(cmd[1]);
                    }
                    server = new Server(port);
                    applicationType = 1;
                    System.out.println("started at port " + port);
                    return;
                } catch (IOException e) {
                    System.out.println("Can't start");
                    return;
                }
            }
            System.out.println("Unsupported command : start");
            return;
        } else {
            if ("stop".equals(command)) {
                if (applicationType == 1) {
                    try {
                        server.stop();
                        applicationType = 0;
                        System.out.println("stopped at port " + server.getPort());
                    } catch (IOException e) {
                        System.out.println("Can't stop");
                    }
                    return;
                }
                System.out.println("Unsupported command : stop");
                return;
            }
            if ("listusers".equals(command)) {
                if (applicationType == 1) {
                    ArrayList<Socket> list = server.listUsers();
                    for (Socket socket : list) {
                        System.out.println(socket.getInetAddress() + ":" + socket.getPort());
                    }
                    return;
                }
                System.out.println("Unsupported command : listusers");
                return;
            }
            if (applicationType == 1) {
                System.out.println("Unsupported command : " + cmd[0]);
                return;
            }

            if ("connect".equals(cmd[0])) {
                if (applicationType != 0) {
                    System.out.println("Already connected");
                    return;
                }
                if (cmd.length != 3) {
                    System.out.println("wrong command format");
                    return;
                }
                try {
                    client = new Client();
                    client.connect(cmd[1], Integer.valueOf(cmd[2]));
                    applicationType = 2;
                    System.out.println("connected");
                } catch (IOException e) {
                    System.out.println("can't connect " + e.getMessage());
                }
                return;
            }
            if ("disconnect".equals(command)) {
                if (applicationType == 2) {
                    try {
                        client.disconnect();
                        client = null;
                        applicationType = 0;
                        System.out.println("diconnected");
                    } catch (IOException e) {
                        System.out.println("can't disconnect " + e.getMessage());
                    }
                    return;
                } else {
                    System.out.println("not connected");
                    return;
                }
            }
            if ("whereami".equals(command)) {
                if (applicationType == 0) {
                    System.out.println("local");
                } else {
                    try {
                        client.send(";");
                        System.out.println("remote " + client.getHost() + " " + client.getPort());
                    } catch (IOException e) {
                        applicationType = 0;
                        System.out.println("local");
                    }
                }
                return;
            }
            if (applicationType == 2) {
                try {
                    client.send(command);
                    System.out.println(client.read());
                } catch (IOException e) {
                    System.out.println("can't connect to the server : " + e.getMessage());
                }
                return;
            }
            runTableCommand(command);
        }
    }

    public void runTableCommand(String command) {
        String[] cmd = command.split("\\s(?![^\\(]*\\))(?![^\\[]*\\])");
        switch (command) {
            case "show tables":
                provider.listTables().forEach(System.out::println);
                return;
            case "get columns count":
                if (currentTable == null) {
                    System.out.println("No tables in usage");
                    return;
                }
                System.out.println(currentTable.getColumnsCount());
                return;
            case "get name":
                if (currentTable == null) {
                    System.out.println("No tables in usage");
                    return;
                }
                System.out.println(currentTable.getName());
                break;
            default:
                try {
                    switch (cmd[0]) {
                        case "use":
                            if (currentTable != null && currentTable.changesNumber() > 0) {
                                System.out.println("Can't use: " + currentTable.changesNumber() + " unsaved shanges");
                                return;
                            }
                            if (provider.getTable(cmd[1]) != null) {
                                currentTable = (CurrentTable) provider.getTable(cmd[1]);
                                System.out.println("Using " + cmd[1]);
                            } else {
                                System.out.println("Table doesn't exist");
                            }
                            break;
                        case "drop":
                            if (provider.getTable(cmd[1]) != null) {
                                if (currentTable == provider.getTable(cmd[1])) {
                                    currentTable = null;
                                }
                                provider.removeTable(cmd[1]);
                            } else {
                                System.out.println(cmd[1] + " doesn't exist");
                            }
                            break;
                        case "create":
                            if (cmd.length != 3) {
                                System.err.println("wrong number of argument to Create");
                                return;
                            }
                            String name = cmd[1];
                            if (provider.getTable(name) != null) {
                                System.out.println("Already exists");
                                return;
                            }
                            String forSignature = cmd[2].replaceFirst("\\(", "");
                            forSignature = forSignature.trim();
                            forSignature = forSignature.substring(0, forSignature.length() - 1);
                            Utils utils = new Utils();
                            ArrayList<Class<?>> signature = utils.signature(forSignature);
                            try {
                                provider.createTable(name, signature);
                                System.out.println("created");
                            } catch (IOException e) {
                                System.err.println("I can't create table" + " " + e.getMessage());
                                System.exit(-1);
                            }
                            break;
                        case "get":
                            if (currentTable == null) {
                                System.out.println("No tables in usage");
                                return;
                            }
                            if (cmd.length == 4 && cmd[1].equals("column") && cmd[2].equals("type")) {
                                System.out.println(currentTable.getColumnType(Integer.parseInt(cmd[3])));
                                return;
                            }
                            currentTable.get(cmd[1]);
                            break;
                        case "put":
                            if (currentTable == null) {
                                System.out.println("No tables in usage");
                                return;
                            }
                            currentTable.put(cmd[1], new Parser().deserialize(currentTable, cmd[2]));
                            break;
                        case "list":
                            if (currentTable == null) {
                                System.out.println("No tables in usage");
                                return;
                            }
                            currentTable.list();
                            break;
                        case "remove":
                            if (currentTable == null) {
                                System.out.println("No tables in usage");
                                return;
                            }
                            currentTable.remove(cmd[1]);
                            break;
                        case "size":
                            if (currentTable == null) {
                                System.out.println("No tables in usage");
                                return;
                            }
                            currentTable.size();
                            break;
                        case "commit":
                            if (currentTable == null) {
                                System.out.println("No tables in usage");
                                return;
                            }
                            currentTable.commit();
                            break;
                        case "rollback":
                            if (currentTable == null) {
                                System.out.println("No tables in usage");
                                return;
                            }
                            currentTable.rollback();
                            break;
                        default:
                            System.out.println("Unknown command");
                            return;
                    }
                } catch (IOException e) {
                    System.out.println("IOException " + e.getMessage());
                    System.exit(-2);
                } catch (ParseException e) {
                    System.out.println("Wrong JSON format. Expected: ");
                    for (int i = 0; i < currentTable.getColumnsCount(); i++) {
                        System.out.print(currentTable.getColumnType(i).getName() + " ");
                    }
                    System.out.println();
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("Wrong command format");
                }
                break;
        }
    }
}
