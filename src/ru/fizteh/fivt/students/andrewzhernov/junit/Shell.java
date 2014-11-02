package ru.fizteh.fivt.students.andrewzhernov.junit;

import java.io.*;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.DirectoryStream;
import java.util.Map;
import java.util.HashMap;

public class Shell {
    private static final String PROMPT = "$ ";
    private static final String STATEMENT_DELIMITER = ";";
    private static final String PARAM_DELIMITER = "\\s+";

    private final Map<String, Command> commands;
    private TableProvider database;

    public Shell(TableProvider database, Command[] commands) throws Exception {
        this.database = database;
        this.commands = new HashMap<>();
        for (Command command : commands) {
            this.commands.put(command.getName(), command);
        }
    }

    public void run(String[] args) throws Exception {
        if (args.length == 0) {
            interactiveMode();
        } else {
            batchMode(args);
        }
    }

    public void interactiveMode() throws Exception {
        Scanner input = new Scanner(System.in);
        while (true) {
            System.out.print(PROMPT);
            try {
                if (input.hasNextLine()) {
                    exec(input.nextLine());
                } else {
                    break;
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        input.close(); 
    }

    public void batchMode(String[] args) throws Exception {
        exec(String.join(";", args));
    }

    public static void removeDir(Path directory) throws IllegalStateException {
        try {
            if (Files.isDirectory(directory)) {
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
                    for (Path entry : stream) {
                        removeDir(entry);
                    }
                }
            }
            if (!directory.toFile().delete()) {
                throw new IllegalStateException("Can't remove " + directory.toString());
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public void exec(String line) throws Exception {
        String[] statements = line.split(STATEMENT_DELIMITER);
        for (String statement : statements) {
            String[] params = statement.trim().split(PARAM_DELIMITER);

            String cmdName = null;
            if (params.length > 0) {
                cmdName = params[0];
                if (cmdName.equals("show") && params.length > 1) {
                    cmdName += (" " + params[1]); 
                }
            }
            Command command = commands.get(cmdName);
            if (command == null) {
                throw new Exception("Command not found: " + cmdName);
            } else {
                command.exec(database, params);
            }
        }
    }
}
