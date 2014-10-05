package ru.fizteh.fivt.students.standy66.filemap;

import com.sun.istack.internal.NotNull;

import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Shell designed to interpret user commands. <br>
 * Created by astepanov on 26.09.14.
 */
public class FileMapShell {

    private Scanner scanner;
    private boolean interactive;
    private Database db;

    public FileMapShell(@NotNull final InputStream inputStream, final boolean isInteractive) {
        scanner = new Scanner(inputStream);
        interactive = isInteractive;
        String dbFile = System.getProperty("db.file");
        if (dbFile == null) {
            System.err.println("No file specified");
            System.exit(5);
        }
        db = new Database(dbFile);
    }

    public void run() {
        while (true) {
            if (interactive) {
                System.out.print("$ ");
                System.out.flush();
            }
            String line;
            try {
                line = scanner.nextLine();
            } catch (NoSuchElementException e) {
                db.flush();
                return;
            }
            String[] actions = line.split(";");

            for (int i = 0; i < actions.length; i++) {
                String[] args =
                        Stream.of(actions[i].split(" "))
                                .filter((s) -> (s.length() > 0))
                                .toArray((size) -> (new String[size]));
                if (args.length == 0) {
                    continue;
                }
                switch (args[0]) {
                    case "put":
                        if (args.length != 3) {
                            System.err.println("Invalid number of arguments");
                            continue;
                        }
                        if (db.get(args[1]) == null) {
                            System.out.println("new");
                        } else {
                            System.out.println("overwrite");
                            System.out.println(db.get(args[1]));
                        }
                        db.put(args[1], args[2]);
                        break;
                    case "get":
                        if (args.length != 2) {
                            System.err.println("Invalid number of arguments");
                            continue;
                        }
                        if (db.get(args[1]) == null) {
                            System.out.println("not found");
                        } else {
                            System.out.println("found");
                            System.out.println(db.get(args[1]));
                        }
                        break;

                    case "remove":
                        if (args.length != 2) {
                            System.err.println("Invalid number of arguments");
                            continue;
                        }
                        if (db.get(args[1]) == null) {
                            System.out.println("not found");
                        } else {
                            System.out.println("removed");
                            db.remove(args[1]);
                        }
                        break;

                    case "list":
                        if (args.length != 1) {
                            System.err.println("Invalid number of arguments");
                            continue;
                        }
                        System.out.println(
                                Stream.of(db.list().toArray(new String[0]))
                                        .collect(Collectors.joining(", ")));
                        break;
                    case "exit":
                        if (args.length != 1) {
                            System.err.println("Invalid number of arguments");
                            continue;
                        }
                        db.flush();
                        System.exit(0);
                        break;

                    default:
                        System.err.println("Unknown command");
                }
            }
        }

    }
}
