package ru.fizteh.fivt.students.andrewzhernov.junit;

import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;

public class Shell {
    private static final String PROMPT = "$ ";
    private static final String STATEMENT_DELIMITER = ";";
    private static final String PARAM_DELIMITER = "\\s+";

    private TableProvider database;
    private Map<String, Command> commands;

    public Shell(TableProvider database, Command[] commands) throws Exception {
        this.database = database;
        this.commands = new HashMap<>();
        for (Command command : commands) {
            this.commands.put(command.getName(), command);
        }
    }

    public void run(String[] args) throws Exception {
        if (args == null) {
            throw new IllegalArgumentException("Invalid commands");
        }
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
                if (!input.hasNextLine()) {
                    break;
                }
                executeLine(input.nextLine());
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        input.close(); 
    }

    public void batchMode(String[] args) throws Exception {
        executeLine(String.join(";", args));
    }

    public void executeLine(String line) throws Exception {
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
                command.execute(database, params);
            }
        }
    }
}
