package ru.fizteh.fivt.students.torunova.storeable.interpreter;

/**
 * Created by nastya on 21.10.14.
 */

import ru.fizteh.fivt.students.torunova.storeable.database.actions.Action;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Pattern;
public class Shell {
    private static final String COMMAND_DELIMITER = ";";
    private static final String PROMPT = "$ ";
    private Map<String, Action> commands;
    private Scanner scanner;
    private PrintWriter writer;
    private boolean interactive;
    private String nameOfExitCommand;

    public Shell(Set<Action> cmds, InputStream is, OutputStream os,
                  String nameOfExitCommand, boolean isInteractive) {
        commands = new HashMap<>();
        for (Action action : cmds) {
            commands.put(action.getName(), action);
        }
        scanner = new Scanner(is);
        writer = new PrintWriter(os, true);
        interactive = isInteractive;
        this.nameOfExitCommand = nameOfExitCommand;

    }

    public boolean run() {
        String nextCommand = null;
        String[] functions;
        while (true) {
            if (interactive) {
                writer.print(PROMPT);
                writer.flush();
            }
            try {
                nextCommand = scanner.nextLine();
            } catch (NoSuchElementException e) {
                String status = "0";
                try {
                    commands.get(nameOfExitCommand).run(status);
                } catch (IOException e1) {
                    writer.println("Caught IOException in exit command");
                    abort();
                    return false;
                } catch (ShellInterruptException e2) {
                    return true;
                }
            }
            functions = nextCommand.split(COMMAND_DELIMITER);
            for (String function : functions) {
                function = function.trim();
                String name = getNameOfFunction(function);
                String parameters = getTheRestOfArguments(function);
                if (commands.containsKey(name)) {
                    boolean res = false;
                    try {
                         res = commands.get(name).run(parameters);
                    } catch (Exception e) {
                        //e.printStackTrace();
                        writer.println("Caught " + e.getClass().getSimpleName() + ": " + e.getMessage());
                        if (!interactive || name.equals(nameOfExitCommand)) {
                            abort();
                            return false;
                        }
                    }
                    if (!interactive && !res) {
                        String status = "1";
                        try {
                            commands.get(nameOfExitCommand).run(status);
                        } catch (IOException e) {
                            writer.println("Caught IOException in exit command");
                            abort();
                            return false;
                        }
                    } else if (!res) {
                        break;
                    }
                } else if (!Pattern.matches("\\s*", name)) {
                    writer.println("Command not found.");
                    if (!interactive) {
                        String status = "1";
                        try {
                            commands.get(nameOfExitCommand).run(status);
                        } catch (IOException e) {
                            writer.println("Caught IOException in exit command");
                            abort();
                            return false;
                        } catch (ShellInterruptException e) {
                            return true;
                        }
                    } else {
                        break;
                    }
                }
            }
        }
    }

    private String getNameOfFunction(String args) {
        if (args.indexOf(" ") < 0) {
            return args.trim();
        }
        return args.substring(0, args.indexOf(" "));
    }

    private String getTheRestOfArguments(String args) {
        if (args.indexOf(" ") < 0) {
            return "";
        }
        return args.substring(args.indexOf(" ") + 1);
    }

    private void abort() {
        writer.println("Aborting...");
    }
}

