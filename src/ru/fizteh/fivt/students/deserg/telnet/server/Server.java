package ru.fizteh.fivt.students.deserg.telnet.server;


import ru.fizteh.fivt.students.deserg.telnet.FileSystem;
import ru.fizteh.fivt.students.deserg.telnet.Program;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by deserg on 11.12.14.
 */
public class Server implements Program {

    private DbTableProvider db;
    private CommonData data;
    private ExecutorService serverService = Executors.newFixedThreadPool(5);

    public Server() {

        String dbDir = System.getProperty("fizteh.db.dir");
        DbTableProviderFactory factory = new DbTableProviderFactory();
        db = (DbTableProvider) factory.create(dbDir);
        if (db == null) {
            System.out.println("Database is null...");
            System.exit(1);
        }
        data = new CommonData(db);

    }

    @Override
    public void work() {

        System.out.println("\nHello! Welcome to deserg DataBase's server!\n");

        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNext()) {
            ArrayList<String> list = FileSystem.readCommandLine(scanner);
            System.out.println(execute(list));
        }
    }

    public String execute(ArrayList<String> arguments) {

        if (arguments.size() < 1 || arguments.size() > 2) {
            return "Wrong command";
        } else {

            String command = arguments.get(0);
            switch (command) {

                case "start": {
                    return commandStart(arguments);
                }

                case "stop": {
                    return commandStop(arguments);
                }

                case "exit": {
                    System.out.println("\nGoodbye!\n");
                    if (data.isStarted()) {
                        serverService.shutdown();
                        data.setStarted(false);
                        data.getDb().close();
                    }
                    System.exit(1);
                    return "";
                }

                case "listusers": {
                    return commandListUsers(arguments);
                }

                default: {
                    return "Wrong command";
                }

            }

        }

    }

    public String commandStart(ArrayList<String> arguments) {

        if (data.isStarted()) {
            return "not started: already started";
        }

        if (arguments.size() == 2) {
            try {
                int port = Integer.valueOf(arguments.get(1));
                data.setPort(port);
            } catch (NumberFormatException ex) {
                return "not started: wrong port number";
            }
        }

        serverService.submit(new ConnectionManager(data));
        data.setStarted(true);
        return "started at port " + data.getPort();

    }

    public String commandStop(ArrayList<String> arguments) {

        if (arguments.size() != 1) {
            return "Too many arguments (required: 0)";
        }


        if (!data.isStarted()) {
            return "not started";
        }

        serverService.shutdown();

        data.getDb().close();
        if (serverService.isShutdown()) {
            data.setStarted(false);
            return "stopped at port " + data.getPort();
        } else {
            return "not stopped :((";
        }
    }

    public String commandListUsers(ArrayList<String> arguments) {

        if (!data.isStarted()) {
            return "not started";
        }

        if (arguments.size() != 1) {
            return "Too many arguments (required: 0)";
        }

        String users = "";
        for (String string: data.getUsers()) {
            users += string + "\n";
        }

        return users.substring(0, users.length() - 1);

    }

}
