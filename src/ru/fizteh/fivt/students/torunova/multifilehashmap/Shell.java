package ru.fizteh.fivt.students.torunova.multifilehashmap;

/**
 * Created by nastya on 21.10.14.
 */

import ru.fizteh.fivt.students.torunova.multifilehashmap.actions.Action;
import ru.fizteh.fivt.students.torunova.multifilehashmap.exceptions.IncorrectDbNameException;
import ru.fizteh.fivt.students.torunova.multifilehashmap.exceptions.IncorrectFileException;
import ru.fizteh.fivt.students.torunova.multifilehashmap.exceptions.TableNotCreatedException;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;
public class Shell {
    private Map<String, Action> commands;
    private Scanner scanner;
    private Database db;
    private boolean interactive;

    public Shell(Set<Action> cmds, InputStream is, String dbfile, boolean isInteractive) {
        commands = new HashMap<>();
        for (Action action : cmds) {
            commands.put(action.getName(), action);
        }
        scanner = new Scanner(is);
        try {
            db = new Database(dbfile);
        } catch (IncorrectDbNameException e) {
            System.err.println("Caught IncorrectDbNameException: " + e.getMessage());
            System.exit(1);
        } catch (IOException e1) {
            System.err.println("Caught IOException: " + e1.getMessage());
            System.exit(1);
        } catch (TableNotCreatedException e2) {
            System.err.println("Caught TableNotCreatedException");
            System.exit(1);
        } catch (IncorrectFileException e3) {
            System.err.println("Caught IncorrectFileException: " + e3.getMessage());
            System.exit(1);
        }
        interactive = isInteractive;
    }

    public void run() {
        String nextCommand = new String();
        String[] functions;
        while (true) {
            if (interactive) {
                System.out.print("$ ");
            }
            try {
                nextCommand = scanner.nextLine();
            } catch (NoSuchElementException e) {
                try {
                    db.close();
                } catch (IOException e1) {
                    System.err.println("Caught IOException: " + e1.getMessage());
                    System.exit(1);
                }
                return;
            }
            functions = nextCommand.split(";");
            for (String function : functions) {
                function = function.trim();
                String[] args = parseArguments(function);
                String name = args[0];
                args = Arrays.copyOfRange(args, 1, args.length);
                if (commands.containsKey(name)) {
                    boolean res = false;
                    try {
                         res = commands.get(name).run(args, db);
                    } catch (IOException e) {
                        System.err.println("Caught IOException: " + e.getMessage());
                        if (!interactive) {
                            System.exit(1);
                        }
                    } catch (TableNotCreatedException e1) {
                        System.err.println("Caught TableNotCreatedException");
                        if (!interactive) {
                            System.exit(1);
                        }
                    } catch (IncorrectFileException e2) {
                        System.err.println("Caught IncorrectFileException: " + e2.getMessage());
                        if (!interactive) {
                            System.exit(1);
                        }
                    }

                    if (!interactive && !res) {
                        try {
                            db.close();
                        } catch (IOException e) {
                            System.err.println("Caught IOException" + e.getMessage());
                            System.exit(1);
                        }
                        System.exit(1);
                    }
                } else if (name.equals("exit")) {
                    /*if (args.length > 0) {
                        System.err.println("exit:too many arguments.");
                    } else {*/
                        try {
                            db.close();
                        } catch (IOException e) {
                            System.err.println("Caught IOException" + e.getMessage());
                            System.exit(1);
                        }
                        System.exit(0);
                    //}
                } else if (!Pattern.matches("\\s+", name)) {
                    System.err.println("Command not found.");
                    if (!interactive) {
                        try {
                            db.close();
                        } catch (IOException e) {
                            System.err.println("Caught IOException" + e.getMessage());
                            System.exit(1);
                        }
                        System.exit(1);
                    }
                }
            }
        }
    }

    private String[] parseArguments(String arg) {
        return arg.split("\\s+");
    }

}

