package ru.fizteh.fivt.students.LevkovMiron.Tellnet;


import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Мирон on 10.11.2014 ru.fizteh.fivt.students.LevkovMiron.Storeable.
 */
public class ConsoleApp {

    private int applicationType;

    private CTable currentT;

    private CTableProvider provider;

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

    public ConsoleApp() {
        File f = new File(System.getProperty("fiztech.db.dir"));
        f.mkdir();
        provider = (CTableProvider) new CTableProviderFactory().create(f.getAbsolutePath());
        applicationType = 0;
    }

    public void runCommand(String command) {
        String[] cmd = command.split("\\s(?![^\\(]*\\))(?![^\\[]*\\])");
        if (cmd[0].equals("start")) {
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
        }
        if (command.equals("stop")) {
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
        if (command.equals("listusers")) {
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

        if (cmd[0].equals("connect")) {
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
        if (command.equals("disconnect")) {
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
        if (command.equals("whereami")) {
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

    public void runTableCommand(String command) {
        String[] cmd = command.split("\\s(?![^\\(]*\\))(?![^\\[]*\\])");
        if (command.equals("show tables")) {
            for (String table : provider.listTables()) {
                System.out.println(table);
            }
            return;
        }
        if (command.equals("get columns count")) {
            if (currentT == null) {
                System.out.println("No tables in usage");
                return;
            }
            System.out.println(currentT.getColumnsCount());
            return;
        }
        if (command.equals("get name")) {
            if (currentT == null) {
                System.out.println("No tables in usage");
                return;
            }
            System.out.println(currentT.getName());
        }
        try {
            if (cmd[0].equals("use")) {
                if (currentT != null && currentT.changesNumber() > 0) {
                    System.out.println("Can't use: " + currentT.changesNumber() + " unsaved shanges");
                    return;
                }
                if (provider.getTable(cmd[1]) != null) {
                    currentT = (CTable) provider.getTable(cmd[1]);
                    System.out.println("Using " + cmd[1]);
                } else {
                    System.out.println("Table doesn't exist");
                }
            } else if (cmd[0].equals("drop")) {
                if (provider.getTable(cmd[1]) != null) {
                    if (currentT == provider.getTable(cmd[1])) {
                        currentT = null;
                    }
                    provider.removeTable(cmd[1]);
                } else {
                    System.out.println(cmd[1] + " doesn't exist");
                }
            } else if (cmd[0].equals("create")) {
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
            } else if (cmd[0].equals("get")) {
                if (currentT == null) {
                    System.out.println("No tables in usage");
                    return;
                }
                if (cmd.length == 4 && cmd[1].equals("column") && cmd[2].equals("type")) {
                    System.out.println(currentT.getColumnType(Integer.parseInt(cmd[3])));
                    return;
                }
                currentT.get(cmd[1]);
            } else if (cmd[0].equals("put")) {
                if (currentT == null) {
                    System.out.println("No tables in usage");
                    return;
                }
                currentT.put(cmd[1], new Parser().deserialize(currentT, cmd[2]));
            } else if (cmd[0].equals("list")) {
                if (currentT == null) {
                    System.out.println("No tables in usage");
                    return;
                }
                currentT.list();
            } else if (cmd[0].equals("remove")) {
                if (currentT == null) {
                    System.out.println("No tables in usage");
                    return;
                }
                currentT.remove(cmd[1]);
            } else if (cmd[0].equals("size")) {
                if (currentT == null) {
                    System.out.println("No tables in usage");
                    return;
                }
                currentT.size();
            } else if (cmd[0].equals("commit")) {
                if (currentT == null) {
                    System.out.println("No tables in usage");
                    return;
                }
                currentT.commit();
            } else if (cmd[0].equals("rollback")) {
                if (currentT == null) {
                    System.out.println("No tables in usage");
                    return;
                }
                currentT.rollback();
            } else {
                System.out.println("Unknown command");
                return;
            }
        } catch (IOException e) {
            System.out.println("IOException " + e.getMessage());
            System.exit(-2);
        } catch (ParseException e) {
            System.out.println("Wrong JSON format. Expected: ");
            for (int i = 0; i < currentT.getColumnsCount(); i++) {
                System.out.print(currentT.getColumnType(i).getName() + " ");
            }
            System.out.println();
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Wrong command format");
        }
    }
}
