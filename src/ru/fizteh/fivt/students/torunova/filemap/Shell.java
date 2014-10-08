package ru.fizteh.fivt.students.torunova.filemap;

import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by nastya on 04.10.14.
 */
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
        db = new FileMap(dbfile);
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
                db.close();
                System.exit(0);
            }
            functions = nextcommand.split(";");
            for (String f : functions) {
                f = f.trim();
                String[] args = parseArguments(f);
                String name = args[0];
                args = Arrays.copyOfRange(args, 1, args.length);
                if (commands.containsKey(name)) {
                    boolean res = commands.get(name).run(args, db);
                    if (!interactive && !res) {
                        System.exit(1);
                    }
                } else if (name.equals("exit")) {
                    if (args.length > 0) {
                        System.err.println("exit:too many arguments.");
                    } else {
                        db.close();
                        System.exit(0);
                    }
                } else if (!Pattern.matches("\\s+", name)) {
                    System.err.println("Command not found.");
                    db.close();
                    System.exit(1);
                }
            }
        }
    }

    private String[] parseArguments(String arg) {
        return arg.split("\\s+");
    }

}
