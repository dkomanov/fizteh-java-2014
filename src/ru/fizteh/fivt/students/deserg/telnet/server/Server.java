package ru.fizteh.fivt.students.deserg.telnet.server;


import ru.fizteh.fivt.students.deserg.telnet.Program;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by deserg on 11.12.14.
 */
public class Server implements Program {

    private DbTableProvider db;
    private CommonData data;
    private ExecutorService serverService = Executors.newFixedThreadPool(5);
    private Future<Integer> serverFuture;
    private boolean started = false;

    public Server() {

        String dbDir = System.getProperty("fizteh.db.dir");
        DbTableProviderFactory factory = new DbTableProviderFactory();
        db = (DbTableProvider) factory.create(dbDir);
        data = new CommonData(db);

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
            System.out.println(execute(arguments));

        }

    }


    private String execute(ArrayList<String> arguments) {

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

        if (started) {
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

        serverFuture = serverService.submit(new ConnectionManager(data));
        return "started at port " + data.port;

    }

    public String commandStop(ArrayList<String> arguments) {

        if (arguments.size() != 1) {
            return "Too many arguments (required: 0)";
        }

        if (!started) {
            return "not started";
        }

        serverService.shutdown();
        if (serverService.isShutdown()) {
            return "stopped at port " + data.port;
        } else {
            return "not stopped :((";
        }
    }

    public String commandListUsers(ArrayList<String> arguments) {

        if (arguments.size() != 1) {
            return "Too many arguments (required: 0)";
        }

        String users = "";
        for (String string: data.users) {
            users += string + "\n";
        }

        return users.substring(0, users.length() - 1);

    }


}
