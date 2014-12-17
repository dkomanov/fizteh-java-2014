package ru.fizteh.fivt.students.torunova.storeable.interpreter;

/**
 * Created by nastya on 21.10.14.
 */

import ru.fizteh.fivt.students.torunova.storeable.database.Database;
import ru.fizteh.fivt.students.torunova.storeable.database.DatabaseWrapper;
import ru.fizteh.fivt.students.torunova.storeable.database.TableHolder;
import ru.fizteh.fivt.students.torunova.storeable.database.actions.Action;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.*;
import java.util.regex.Pattern;
public class Shell {
    private Map<String, Action> commands;
    private Scanner scanner;
    private TableHolder currentTable;
    private boolean interactive;
    private String nameOfExitCommand;

    public Shell(Set<Action> cmds, InputStream is, OutputStream os, TableHolder currentTable, String nameOfExitCommand, boolean isInteractive) {
        commands = new HashMap<>();
        for (Action action : cmds) {
            commands.put(action.getName(), action);
        }
        scanner = new Scanner(is);
        this.currentTable = currentTable;
        interactive = isInteractive;
        this.nameOfExitCommand = nameOfExitCommand;

    }

    public void run() {
        String nextCommand;
        String[] functions;
        while (true) {
            if (interactive) {
                System.out.print("$ ");
            }
            try {
                nextCommand = scanner.nextLine();
            } catch (NoSuchElementException e) {
                if (currentTable.get() != null) {
                    currentTable.get().commit();
                }
                return;
            }
            functions = nextCommand.split(";");
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
                        System.err.println("Caught " + e.getClass().getSimpleName() + ": " + e.getMessage());
                        if (!interactive || name.equals(nameOfExitCommand)) {
                            abort();
                        }
                    }
                    if (!interactive && !res) {
                        if (currentTable.get() != null) {
                            currentTable.get().commit();
                        }
                        System.exit(1);
                    } else if (!res) {
                        break;
                    }
                } else if (!Pattern.matches("\\s*", name)) {
                    System.err.println("Command not found.");
                    if (!interactive) {
                        if (currentTable.get() != null) {
                            currentTable.get().commit();
                        }
                            System.exit(1);

                    } else {
                        break;
                    }
                }
            }
        }
    }

    private String getNameOfFunction(String args) {
        return args.substring(0, args.indexOf(" "));
    }

    private String getTheRestOfArguments(String args) {
        return args.substring(args.indexOf(" ") + 1);
    }

    private void abort() {
        System.err.println("Aborting...");
        System.exit(1);
    }
}

