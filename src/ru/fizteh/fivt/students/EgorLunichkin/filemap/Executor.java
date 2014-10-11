package ru.fizteh.fivt.students.EgorLunichkin.filemap;

import java.util.Scanner;

public class Executor {
    public Executor(String[] args) {
        try {
            this.dataBase = new DataBase(System.getProperty("db.file"));
        } catch (FileMapException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        }
        if (args.length == 0) {
            try {
                interactiveMode();
            } catch (FileMapException ex) {
                System.err.println(ex.getMessage());
                System.exit(1);
            }
        }
        else {
            try {
                packageMode(args);
            } catch (FileMapException ex) {
                System.err.println(ex.getMessage());
                System.exit(1);
            }
        }
        System.exit(0);
    }

    private DataBase dataBase;

    private void interactiveMode() throws FileMapException {
        Scanner in = new Scanner(System.in);
        System.out.print("$ ");
        while (in.hasNextLine()) {
            String line = in.nextLine();
            String[] commands = line.trim().split(";");
            for (String command : commands) {
                executeCommand(command);
            }
            System.out.print("$ ");
        }
        in.close();
        System.out.close();
    }

    private void packageMode(String[] args) throws FileMapException {
        StringBuilder line = new StringBuilder();
        for (String arg : args) {
            line.append(arg + ' ');
        }
        String[] commands = line.toString().trim().split(";");
        for (String command : commands) {
            executeCommand(command);
        }
    }

    private void executeCommand(String cmd) throws FileMapException {
        String[] command = cmd.trim().split("\\s+");
        Command exec;
        switch(command[0]) {
            case "put":
                if (command.length > 3) {
                    throw new FileMapException("put: Too many arguments");
                }
                if (command.length < 3) {
                    throw new FileMapException("put: Too few arguments");
                }
                exec = new PutCommand(dataBase, command[1], command[2]);
                break;
            case "get":
                if (command.length > 2) {
                    throw new FileMapException("get: Too many arguments");
                }
                if (command.length < 2) {
                    throw new FileMapException("get: Too few arguments");
                }
                exec = new GetCommand(dataBase, command[1]);
                break;
            case "remove":
                if (command.length > 2) {
                    throw new FileMapException("remove: Too many arguments");
                }
                if (command.length < 2) {
                    throw new FileMapException("remove: Too few arguments");
                }
                exec = new RemoveCommand(dataBase, command[1]);
                break;
            case "list":
                if (command.length > 1) {
                    throw new FileMapException("list: Too many arguments");
                }
                exec = new ListCommand(dataBase);
                break;
            case "exit":
                if (command.length > 1) {
                    throw new FileMapException("exit: Too many arguments");
                }
                exec = new ExitCommand();
                break;
            default:
                throw new FileMapException("Unknown command");
        }
        exec.run();
    }
}
