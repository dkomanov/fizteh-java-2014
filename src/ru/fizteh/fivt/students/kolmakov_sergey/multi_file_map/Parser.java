package ru.fizteh.fivt.students.kolmakov_sergey.multi_file_map;

import java.util.Scanner;

public class Parser {
    public static boolean batchMode;

    protected static void batchMode(String[] args) throws DatabaseExitException {
        batchMode = true;
        StringBuilder builder = new StringBuilder();
        for (String current : args) {
            builder.append(current);
            builder.append(" ");
        }
        String[] commandLine = builder.toString().split(";");
        for (String currentCommand : commandLine) {
            parse(currentCommand.trim().split("\\s+"));
        }
        throw new DatabaseExitException(0);
    }

    protected static void interactiveMode() throws DatabaseExitException {
        batchMode = false;
        String[] commandLine;
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("$ ");
                commandLine = scanner.nextLine().trim().split(";");
                for (String currentCommand : commandLine) {
                    parse(currentCommand.trim().split("\\s+"));
                }
            }
        }
    }

    private static void parse(String[] command) throws DatabaseExitException {
        try {
            if (command.length > 0 && !command[0].isEmpty()) {
                switch(command[0]) {
                    case "use":
                        CommandInterpreter.useTable(command);
                        break;
                    case "create":
                        CommandInterpreter.create(command);
                        break;
                    case "drop":
                        CommandInterpreter.drop(command);
                        break;
                    case "show":
                        if (command.length > 1 && command[1].equals("tables")) {
                            CommandInterpreter.showTables(command);
                        } else {
                            throw new
                                    IllegalArgumentException("No such command declared");
                        }
                        break;
                    case "put":
                        CommandInterpreter.put(command);
                        break;
                    case "get":
                        CommandInterpreter.get(command);
                        break;
                    case "remove":
                        CommandInterpreter.remove(command);
                        break;
                    case "list":
                        CommandInterpreter.list(command);
                        break;
                    case "exit":
                        CommandInterpreter.exit(command);
                        break;
                    default:
                        System.out.println("No such command declared");
                }
            }
        } catch (IllegalArgumentException e){
            System.out.println(e.getMessage());
        }
        catch (DatabaseExitException e){
            System.exit(e.status);
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }
}
