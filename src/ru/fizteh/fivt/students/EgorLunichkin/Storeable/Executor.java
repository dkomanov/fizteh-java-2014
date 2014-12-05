package ru.fizteh.fivt.students.EgorLunichkin.Storeable;

import ru.fizteh.fivt.students.EgorLunichkin.Storeable.commands.*;

import java.util.Arrays;
import java.util.Scanner;

public class Executor {
    public Executor(String[] args) throws Exception {
        String dbPath = "db";
        sTableProvider = new StoreableTableProvider(dbPath);
        if (args.length == 0) {
            interactiveMode();
        } else {
            packageMode(args);
        }
    }

    private StoreableTableProvider sTableProvider;

    private void interactiveMode() throws StoreableException {
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

    private void packageMode(String[] args) throws StoreableException {
        StringBuilder line = new StringBuilder();
        for (String arg : args) {
            line.append(arg + ' ');
        }
        String[] commands = line.toString().trim().split(";");
        for (String command : commands) {
            executeCommand(command);
        }
    }

    private void executeCommand(String cmd) throws StoreableException {
        String[] command = cmd.trim().split("\\s+");
        Command exec;
        switch (command[0]) {
            case "put":
                if (command.length > 3) {
                    throw new StoreableException("put: Too many arguments");
                }
                if (command.length < 3) {
                    throw new StoreableException("put: Too few arguments");
                }
                exec = new PutCommand(sTableProvider, command[1], command[2]);
                break;
            case "get":
                if (command.length > 2) {
                    throw new StoreableException("get: Too many arguments");
                }
                if (command.length < 2) {
                    throw new StoreableException("get: Too few arguments");
                }
                exec = new GetCommand(sTableProvider, command[1]);
                break;
            case "remove":
                if (command.length > 2) {
                    throw new StoreableException("remove: Too many arguments");
                }
                if (command.length < 2) {
                    throw new StoreableException("remove: Too few arguments");
                }
                exec = new RemoveCommand(sTableProvider, command[1]);
                break;
            case "list":
                if (command.length > 1) {
                    throw new StoreableException("list: Too many arguments");
                }
                exec = new ListCommand(sTableProvider);
                break;
            case "create":
                if (command.length < 3) {
                    throw new StoreableException("create: Too few arguments");
                }
                exec = new CreateCommand(sTableProvider, command[1], Arrays.copyOfRange(command, 2, command.length));
                break;
            case "drop":
                if (command.length > 2) {
                    throw new StoreableException("drop: Too many arguments");
                }
                if (command.length < 2) {
                    throw new StoreableException("drop: Too few arguments");
                }
                exec = new DropCommand(sTableProvider, command[1]);
                break;
            case "use":
                if (command.length > 2) {
                    throw new StoreableException("use: Too many arguments");
                }
                if (command.length < 2) {
                    throw new StoreableException("use: Too few arguments");
                }
                exec = new UseCommand(sTableProvider, command[1]);
                break;
            case "show":
                if (command.length < 2 || !command[1].equals("tables")) {
                    throw new StoreableException("Unknown command");
                }
                if (command.length > 2) {
                    throw new StoreableException("show tables: Too many arguments");
                }
                exec = new ShowTablesCommand(sTableProvider);
                break;
            case "commit":
                if (command.length > 1) {
                    throw new StoreableException("commit: Too many arguments");
                }
                exec = new CommitCommand(sTableProvider);
                break;
            case "rollback":
                if (command.length > 1) {
                    throw new StoreableException("rollback: Too many arguments");
                }
                exec = new RollbackCommand(sTableProvider);
                break;
            case "exit":
                if (command.length > 1) {
                    throw new StoreableException("exit: Too many arguments");
                }
                exec = new ExitCommand();
                break;
            default:
                throw new StoreableException("Unknown command");
        }
        exec.run();
    }
}
