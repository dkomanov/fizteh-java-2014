package ru.fizteh.fivt.students.EgorLunichkin.Parallel;

import ru.fizteh.fivt.students.EgorLunichkin.Parallel.commands.*;

import java.util.Arrays;
import java.util.Scanner;

public class Executor {
    public Executor(String[] args) throws Exception {
        String dbPath = "db";
        tableProvider = new ParallelTableProvider(dbPath);
        if (args.length == 0) {
            interactiveMode();
        } else {
            packageMode(args);
        }
    }

    private ParallelTableProvider tableProvider;

    private void interactiveMode() throws ParallelException {
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

    private void packageMode(String[] args) throws ParallelException {
        StringBuilder line = new StringBuilder();
        for (String arg : args) {
            line.append(arg + ' ');
        }
        String[] commands = line.toString().trim().split(";");
        for (String command : commands) {
            executeCommand(command);
        }
    }

    private void executeCommand(String cmd) throws ParallelException {
        String[] command = cmd.trim().split("\\s+");
        Command exec;
        switch (command[0]) {
            case "put":
                if (command.length > 3) {
                    throw new ParallelException("put: Too many arguments");
                }
                if (command.length < 3) {
                    throw new ParallelException("put: Too few arguments");
                }
                exec = new PutCommand(tableProvider, command[1], command[2]);
                break;
            case "get":
                if (command.length > 2) {
                    throw new ParallelException("get: Too many arguments");
                }
                if (command.length < 2) {
                    throw new ParallelException("get: Too few arguments");
                }
                exec = new GetCommand(tableProvider, command[1]);
                break;
            case "remove":
                if (command.length > 2) {
                    throw new ParallelException("remove: Too many arguments");
                }
                if (command.length < 2) {
                    throw new ParallelException("remove: Too few arguments");
                }
                exec = new RemoveCommand(tableProvider, command[1]);
                break;
            case "list":
                if (command.length > 1) {
                    throw new ParallelException("list: Too many arguments");
                }
                exec = new ListCommand(tableProvider);
                break;
            case "create":
                if (command.length < 3) {
                    throw new ParallelException("create: Too few arguments");
                }
                exec = new CreateCommand(tableProvider, command[1], Arrays.copyOfRange(command, 2, command.length));
                break;
            case "drop":
                if (command.length > 2) {
                    throw new ParallelException("drop: Too many arguments");
                }
                if (command.length < 2) {
                    throw new ParallelException("drop: Too few arguments");
                }
                exec = new DropCommand(tableProvider, command[1]);
                break;
            case "use":
                if (command.length > 2) {
                    throw new ParallelException("use: Too many arguments");
                }
                if (command.length < 2) {
                    throw new ParallelException("use: Too few arguments");
                }
                exec = new UseCommand(tableProvider, command[1]);
                break;
            case "show":
                if (command.length < 2 || !command[1].equals("tables")) {
                    throw new ParallelException("Unknown command");
                }
                if (command.length > 2) {
                    throw new ParallelException("show tables: Too many arguments");
                }
                exec = new ShowTablesCommand(tableProvider);
                break;
            case "commit":
                if (command.length > 1) {
                    throw new ParallelException("commit: Too many arguments");
                }
                exec = new CommitCommand(tableProvider);
                break;
            case "rollback":
                if (command.length > 1) {
                    throw new ParallelException("rollback: Too many arguments");
                }
                exec = new RollbackCommand(tableProvider);
                break;
            case "exit":
                if (command.length > 1) {
                    throw new ParallelException("exit: Too many arguments");
                }
                exec = new ExitCommand();
                break;
            default:
                throw new ParallelException("Unknown command");
        }
        exec.run();
    }
}
