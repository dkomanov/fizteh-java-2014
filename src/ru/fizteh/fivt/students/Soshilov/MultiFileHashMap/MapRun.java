package ru.fizteh.fivt.students.Soshilov.MultiFileHashMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: soshikan
 * Date: 22 October 2014
 * Time: 22:29
 */
public class MapRun {
    /**
     * Map where every function is. Then we put the command of by entering the key.
     */
    protected static Map<String, Command> commandMap = new HashMap<>();
    /**
     * Database we use.
     */
    protected static DataBase db;

    private static String signOfInvitation = "$ ";

    /**
     * Fill db and commandMap before the object of the class would be made.
     */
    static {
        Path path = Paths.get(System.getProperty("user.dir")).resolve(System.getProperty("fizteh.db.dir"));
        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException ex) {
                System.out.println("Unable to create database directory (" + path.toString() + ")");
            }
        } else if (!Files.isDirectory(path)) {
            System.out.println("Database directory (" + path.toString() + ") is a file!");
            System.exit(1);
        }

        db = new DataBase(path);

        commandMap.put("exit", new Exit());
        commandMap.put("put", new TablePut());
        commandMap.put("get", new TableGet());
        commandMap.put("remove", new TableRemove());
        commandMap.put("list", new TableList());
        commandMap.put("create", new DataBaseCreate());
        commandMap.put("drop", new DataBaseDrop());
        commandMap.put("use", new DataBaseUse());
        commandMap.put("show", new DataBaseShowTables());
    }

    /**
     * We switch commands into different classes.
     * @param args Commands that were entered: name, its' arguments.
     */
    public static void run(final String[] args) {
        if (args.length == 0) {
            throw new RuntimeException("no command");
        }

        Command command = commandMap.get(args[0]);
        if (command == null) {
            System.err.println(args[0] + ": command not found");
            System.exit(1);
        } else {
            command.execute(args, db);
        }
    }

    /**
     * Interactive Mode: do entered functions.
     */
    public static void interactiveMode() {
        try (Scanner sc = new Scanner(System.in)) {
            while (true) {
                System.out.print(signOfInvitation);
                String currentString = sc.nextLine();
                currentString = currentString.trim();
                run(currentString.split("\\s+"));
            }
        }
    }

    /**
     * Package mode: we enter command constantly.
     * @param args Commands that were entered: name, its' arguments.
     */
    public static void batchMode(final String[] args) {
        StringBuilder builder = new StringBuilder();
        for (String s : args) {
            builder.append(s).append(" ");
        }
        String string = new String(builder);
        String[] commands = string.split("\\s*;\\s*");
        for (String commandParams : commands) {
            run(commandParams.split("\\s+"));
        }
        System.exit(0);
    }

    /**
     * The main function, which do the program: first we see, interactive or package mode is.
     * @param args Commands that were entered: name, its' arguments.
     */
    public static void main(final String[] args) {
        if (args.length == 0) {
            interactiveMode();
        } else {
            batchMode(args);
        }
    }
}
