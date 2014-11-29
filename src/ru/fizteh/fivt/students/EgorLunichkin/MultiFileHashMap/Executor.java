package ru.fizteh.fivt.students.EgorLunichkin.MultiFileHashMap;

import java.util.Scanner;

public class Executor {
    public Executor(String[] args) throws Exception {
        String dbDir = "db";
        multiDataBase = new MultiDataBase(dbDir);
        if (args.length == 0) {
            interactiveMode();
        } else {
            packageMode(args);
        }
    }

    private MultiDataBase multiDataBase;

    private void interactiveMode() throws Exception {
        Scanner in = new Scanner(System.in);
        System.out.print("$ ");
        while (in.hasNextLine()) {
            String[] commands = in.nextLine().trim().split(";");
            for (String command : commands) {
                executeCommand(command);
            }
            System.out.print("$ ");
        }
        in.close();
        System.out.close();
    }

    private void packageMode(String[] args) throws Exception {
        StringBuilder line = new StringBuilder();
        for (String arg : args) {
            line.append(arg + ' ');
        }
        String[] commands = line.toString().trim().split(";");
        for (String command : commands) {
            executeCommand(command);
        }
    }

    private void executeCommand(String cmd) throws Exception {
        String[] command = cmd.trim().split("\\s+");
        Command exec;
        switch (command[0]) {
            case "put":
                if (command.length > 3) {
                    throw new MultiFileHashMapException("put: Too many arguments");
                }
                if (command.length < 3) {
                    throw new MultiFileHashMapException("put: Too few arguments");
                }
                exec = new MultiPutCommand(multiDataBase, command[1], command[2]);
                break;
            case "get":
                if (command.length > 2) {
                    throw new MultiFileHashMapException("get: Too many arguments");
                }
                if (command.length < 2) {
                    throw new MultiFileHashMapException("get: Too few arguments");
                }
                exec = new MultiGetCommand(multiDataBase, command[1]);
                break;
            case "remove":
                if (command.length > 2) {
                    throw new MultiFileHashMapException("remove: Too many arguments");
                }
                if (command.length < 2) {
                    throw new MultiFileHashMapException("remove: Too few arguments");
                }
                exec = new MultiRemoveCommand(multiDataBase, command[1]);
                break;
            case "list":
                if (command.length > 1) {
                    throw new MultiFileHashMapException("list: Too many arguments");
                }
                exec = new MultiListCommand(multiDataBase);
                break;
            case "create":
                if (command.length > 2) {
                    throw new MultiFileHashMapException("create: Too many arguments");
                }
                if (command.length < 2) {
                    throw new MultiFileHashMapException("create: Too few arguments");
                }
                exec = new CreateCommand(multiDataBase, command[1]);
                break;
            case "drop":
                if (command.length > 2) {
                    throw new MultiFileHashMapException("drop: Too many arguments");
                }
                if (command.length < 2) {
                    throw new MultiFileHashMapException("drop: Too few arguments");
                }
                exec = new DropCommand(multiDataBase, command[1]);
                break;
            case "use":
                if (command.length > 2) {
                    throw new MultiFileHashMapException("use: Too many arguments");
                }
                if (command.length < 2) {
                    throw new MultiFileHashMapException("use: Too few agruments");
                }
                exec = new UseCommand(multiDataBase, command[1]);
                break;
            case "show":
                if (command.length < 2 || !command[1].equals("tables")) {
                    throw new MultiFileHashMapException("Unknown command");
                }
                if (command.length > 2) {
                    throw new MultiFileHashMapException("show tables: Too many arguments");
                }
                exec = new ShowTablesCommand(multiDataBase);
                break;
            case "exit":
                if (command.length > 1) {
                    throw new MultiFileHashMapException("exit: Too many arguments");
                }
                exec = new ExitCommand();
                break;
            default:
                throw new MultiFileHashMapException("Unknown command");
        }
        exec.run();
    }
}
