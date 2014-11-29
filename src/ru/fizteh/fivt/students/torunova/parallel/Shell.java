package ru.fizteh.fivt.students.torunova.parallel;

/**
 * Created by nastya on 21.10.14.
 */

import ru.fizteh.fivt.students.torunova.parallel.actions.Action;
import ru.fizteh.fivt.students.torunova.parallel.exceptions.IncorrectDbException;
import ru.fizteh.fivt.students.torunova.parallel.exceptions.IncorrectDbNameException;
import ru.fizteh.fivt.students.torunova.parallel.exceptions.IncorrectFileException;
import ru.fizteh.fivt.students.torunova.parallel.exceptions.TableNotCreatedException;

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
            abort();
        } catch (IOException e1) {
            System.err.println("Caught IOException: " + e1.getMessage());
            abort();
        } catch (TableNotCreatedException e2) {
            if (e2.getMessage() == null || e2.getMessage().isEmpty()) {
                System.err.println("Caught TableNotCreatedException");
            } else {
                System.err.println(e2.getMessage());
            }
            abort();
        } catch (IncorrectFileException e3) {
            System.err.println(e3.getMessage());
            abort();
        } catch (IncorrectDbException e4) {
            System.err.println(e4.getMessage());
        }
        interactive = isInteractive;
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
                if (db.currentTable != null) {
                    db.currentTable.commit();
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
                        if (name.equals("create")) {
                            args = argsForCreate(function);
                        }
                         res = commands.get(name).run(args, new DatabaseWrapper(db));
                    } catch (IOException e) {
                        System.err.println("Caught IOException: " + e.getMessage());
                        if (!interactive || name.equals("exit")) {
                            abort();
                        }
                    } catch (TableNotCreatedException e1) {
                        String message = e1.getMessage();
                        if (message == null || message.isEmpty()) {
                            System.err.println("Caught TableNotCreatedException");
                        } else {
                            System.err.println(message);
                        }
                        if (!interactive || name.equals("exit")) {
                            abort();
                        }
                    } catch (IncorrectFileException e2) {
                        System.err.println(e2.getMessage());
                        if (!interactive || name.equals("exit")) {
                            abort();
                        }
                    } catch (RuntimeException e3) {
                        e3.printStackTrace();
                        System.err.println(e3.getMessage());
                        if (!interactive || name.equals("exit")) {
                            abort();
                        }
                    }
                    if (!interactive && !res) {
                        if (db.currentTable != null) {
                            db.currentTable.commit();
                        }
                        System.exit(1);
                    } else if (!res) {
                        break;
                    }
                } else if (!Pattern.matches("\\s*", name)) {
                    System.err.println("Command not found.");
                    if (!interactive) {
                        if (db.currentTable != null) {
                            db.currentTable.commit();
                        }
                            System.exit(1);

                    } else {
                        break;
                    }
                }
            }
        }
    }

    private String[] argsForCreate(String args) {
        args = args.replaceAll("\\s+", " ");
        String typesWithoutBrakets;
        String tableName;
        try {
            String newArgs = args.substring(args.indexOf(' ') + 1);
            tableName = newArgs.substring(0, newArgs.indexOf(' '));
            String argsWithoutTableName = newArgs.substring(newArgs.indexOf(' '));
            typesWithoutBrakets = argsWithoutTableName.substring(argsWithoutTableName.indexOf('(') + 1,
                                                                 argsWithoutTableName.lastIndexOf(')'));
        } catch (StringIndexOutOfBoundsException e) {
            throw new RuntimeException("create: wrong command format");
        }
        StringBuilder builder = new StringBuilder();
        builder = builder.append(tableName + ' ').append(typesWithoutBrakets);
        return builder.toString().trim().split("\\s+");
    }

    private String[] parseArguments(String arg) {
        return arg.split("\\s+");
    }
    private void abort() {
        System.err.println("Aborting...");
        System.exit(1);
    }
}

