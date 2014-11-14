package ru.fizteh.fivt.students.oscar_nasibullin.MultiFileHashMap;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// DataBaseShell for DataBase class.

public final class DataBaseShell {

    private static DataBase dataBase;

    private DataBaseShell() {
        // Disable instantiation to this class.
    }

    public static void main(final String[] args) {
        try {
            if (!Paths.get(System.getProperty("fizteh.db.dir")).toFile().exists()) {
                throw new Exception("not found");
            }
        } catch (Exception e) {
            System.err.println("database root directory error: " + e.getMessage());
            System.exit(1);
        }

        if (args.length > 0) {
            runBatch(args);
        } else {
            runInteractive();
        }
    }

    public static void runInteractive() {
        List<List<String>> commands = new ArrayList<>();
        try (Scanner in = new Scanner(System.in)) {
            // "Try with resources" doesn't have to have a catch block.
            while (true) {
                System.out.print("$ ");
                String input = null;
                input = in.nextLine();
                String[] args = new String[1];
                args[0] = input;
                commands = parse(args);
                activator(commands, false);
            }
        }
    }

    public static void runBatch(final String[] args) {
        List<List<String>> commands = new ArrayList<>();

        commands = parse(args);
        activator(commands, true);

        System.exit(0);
    }

    public static void activator(final List<List<String>> commands,
                                 final boolean batchMode) {
        boolean exitWithError = false;
        String rezultMessage = null;
        try {
            if (dataBase == null) {
                dataBase = new DataBase();
            }
            for (int i = 0; i < commands.size(); i++) {
                switch (commands.get(i).get(0)) {
                    case "exit":
                        exitWithError = true;
                        dataBase.close();
                        System.exit(0);
                        break;
                    case "create":
                        rezultMessage = dataBase.create(commands.get(i));
                        break;
                    case "drop":
                        rezultMessage = dataBase.drop(commands.get(i));
                        break;
                    case "use":
                        rezultMessage = dataBase.use(commands.get(i));
                        break;
                    case "show":
                        if ((commands.get(i).size() != 1) && (commands.get(i).get(1).equals("tables"))) { // ������� :)
                            rezultMessage = dataBase.showTables(commands.get(i));
                        } else {
                            System.err.println("show: no such command");
                        }
                        break;
                    case "put":
                        rezultMessage = dataBase.getTable().put(commands.get(i));
                        break;
                    case "get":
                        rezultMessage = dataBase.getTable().get(commands.get(i));
                        break;
                    case "remove":
                        rezultMessage = dataBase.getTable().remove(commands.get(i));
                        break;
                    case "list":
                        rezultMessage = dataBase.getTable().list(commands.get(i));
                        break;
                    default:
                        System.err.println(commands.get(i).get(0)
                                + ": no such command");
                        if (batchMode) {
                            dataBase.close();
                            System.exit(1);
                        }
                }
                if (rezultMessage != null) {
                    System.out.println(rezultMessage);
                }
            }
        } catch (Exception e) {
            if (e.getMessage() != null) {
                System.err.println(e.getMessage());
            }
            if (batchMode || dataBase == null || exitWithError) {
                System.exit(1);
            }
        }
    }

    public static List<List<String>> parse(final String[] args) {
        List<List<String>> commands = new ArrayList<>();
        ArrayList<String> comAndArgs = new ArrayList<>();
        String[] arguments;

        if (args.length == 1) { // Interactive parse.
            arguments = args[0].split(";");
            for (String arg : arguments) {
                comAndArgs = new ArrayList<String>(Arrays.asList(arg.split(" ")));
                if (comAndArgs.size() != 0) {
                    commands.add(new ArrayList<String>(comAndArgs));
                }
                comAndArgs.clear();
            }
        } else {                // Batch.
            arguments = args;
            for (int i = 0; i < arguments.length; i++) {
                if (arguments[i].endsWith(";")) {
                    int start = 0;
                    int end = arguments[i].length() - 1;
                    char[] buf = new char[end];
                    arguments[i].getChars(start, end, buf, 0);
                    String lastArg = new String(buf);

                    comAndArgs.add(lastArg);
                    commands.add(new ArrayList<>(comAndArgs));
                    comAndArgs.clear();
                } else {
                    comAndArgs.add(arguments[i]);
                    if (i == arguments.length - 1) {
                        commands.add(comAndArgs);
                    }
                }
            }
        }
        return commands;
    }

}
