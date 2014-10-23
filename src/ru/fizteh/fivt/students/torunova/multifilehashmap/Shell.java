package ru.fizteh.fivt.students.torunova.multifilehashmap;

/**
 * Created by nastya on 21.10.14.
 */

import ru.fizteh.fivt.students.torunova.multifilehashmap.actions.Action;
import ru.fizteh.fivt.students.torunova.multifilehashmap.exceptions.IncorrectFileException;
import ru.fizteh.fivt.students.torunova.multifilehashmap.exceptions.TableNotCreatedException;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;
public class Shell {
    HashMap<String, Action> commands;
    Scanner scanner;
    Database db;
    boolean interactive;

    public Shell(Set<Action> cmds, InputStream is, String dbfile, boolean isInteractive) {
        commands = new HashMap<>();
        for (Action a : cmds) {
            commands.put(a.getName(), a);
        }
        scanner = new Scanner(is);
        db = new Database(dbfile);
        interactive = isInteractive;
    }

    public void run() {
        String nextcommand = new String();
        String[] functions;
        while (true) {
            if (interactive) {
                System.out.print("$ ");
            }
            try {
                nextcommand = scanner.nextLine();
            } catch (NoSuchElementException e) {
                try {
                    db.close();
                } catch (IOException e1) {
                    System.err.println("Caught IOException: " + e1.getMessage());
                    System.exit(1);
                }
                System.exit(0);
            }
            functions = nextcommand.split(";");
            for (String f : functions) {
                f = f.trim();
                String[] args = parseArguments(f);
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
                    if (args.length > 0) {
                        System.err.println("exit:too many arguments.");
                    } else {
                        try {
                            db.close();
                        } catch (IOException e) {
                            System.err.println("Caught IOException" + e.getMessage());
                            System.exit(1);
                        }
                        System.exit(0);
                    }
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

