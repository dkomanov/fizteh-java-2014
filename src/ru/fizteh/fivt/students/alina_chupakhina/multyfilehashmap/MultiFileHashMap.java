package ru.fizteh.fivt.students.alina_chupakhina.multyfilehashmap;

import java.io.*;
import java.util.*;

public class MultiFileHashMap {

    private static String path; //way to directory
    private static boolean out;
    private static Table currentTable;
    private static Map<String, Integer> tableList;
    private static final String INVALID_NUMBER_OF_ARGUMENTS_MESSAGE
            = "Invalid number of arguments";

    public static void main(final String[] args) {
        try {
            path = System.getProperty("fizteh.db.dir");
            if (path == null) {
                throw new Exception("Enter directory"); 
            }
            if (!dir.exists() || !dir.isDirectory()) {
                throw new Exception("directory not exist");
            }
            tableList = new TreeMap<String, Integer>();
            File dir = new File(path);
            File[] children = dir.listFiles();
            int j = 0;
            while (j < children.length) {
                Table t = new Table(children[j].getName(), path);
                tableList.put(children[j].getName(), t.getNumberOfElements());
                j++;
                t.exit();
            }
            if (args.length > 0) {
                batch(args);
            } else {
                interactive();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    public static void interactive() {
        out = false;
        Scanner sc = new Scanner(System.in);
        try {
            while (!out) {
                System.out.print("$ ");
                String s = sc.nextLine();
                doCommand(s, false);
            }
            System.exit(0);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void batch(final String[] args) {
        String arg;
        arg = args[0];
        for (int i = 1; i != args.length; i++) {
            arg = arg + ' ' + args[i];
        }
        String[] commands = arg.trim().split(";");
        try {
            for (int i = 0; i != commands.length; i++) {
                doCommand(commands[i], true);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
        interactive();
    }

    public static void doCommand(final String command, final boolean isBatch)
            throws Exception {
        String[] args = command.trim().split("\\s+");
        try {
            if (args[0].equals("create")) {
                create(args);
            } else if (args[0].equals("drop")) {
                drop(args);
            } else if (args[0].equals("use")) {
                use(args);
            } else if (args[0].equals("show")) {
                showtables(args);
            } else if (args[0].equals("put")) {
                if (currentTable == null) {
                    System.out.println("no table");
                } else {
                    put(args);
                }
            } else if (args[0].equals("get")) {
                if (currentTable == null) {
                    System.out.println("no table");
                } else {
                    currentTable.get(args);
                }
            } else if (args[0].equals("remove")) {
                if (currentTable == null) {
                    System.out.println("no table");
                } else {
                    remove(args);
                }
            } else if (args[0].equals("list")) {
                if (currentTable == null) {
                    System.out.println("no table");
                } else {
                    currentTable.list(args);
                }
            } else if (args[0].equals("exit")) {
                exit(args);
            } else if (args[0].equals("")) {
                out = false;
            } else {
                throw new Exception(args[0] + "Invalid command");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            if (isBatch) {
                System.exit(-1);
            }
        }
    }

    public static void put(final String[] args) throws Exception {
        currentTable.put(args);
        tableList.put(currentTable.getName(),
                currentTable.getNumberOfElements());
    }

    public static void remove(final String[] args) throws Exception {
        currentTable.remove(args);
        tableList.put(currentTable.getName(),
                currentTable.getNumberOfElements());
    }

    public static void create(String[] args) throws Exception {
        if (args.length != 2) {
            throw new IllegalArgumentException("create: "
                    + INVALID_NUMBER_OF_ARGUMENTS_MESSAGE);
        }
        String pathToTable = path + File.separator + args[1];
        File table = new File(pathToTable);
        if (table.exists() && table.isDirectory()) {
            System.out.println(args[1] + " exists");
        } else {
            table.mkdir();
            Table t = new Table(args[1], path);
            tableList.put(args[1], 0);
            System.out.println("created");
        }
    }

    public static void drop(final String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("drop: "
                    + INVALID_NUMBER_OF_ARGUMENTS_MESSAGE);
        }
        String pathToTable = path + File.separator + args[1];
        File table = new File(pathToTable);
        if (!table.exists() || !table.isDirectory()) {
            System.out.println(args[1] + " not exists");
        } else {
            tableList.remove(args[1]);
            table.delete();
            System.out.println("dropped");
        }
    }

    public static void use(String[] args) throws Exception {
        if (args.length != 2) {
            throw new IllegalArgumentException("use: "
                    + INVALID_NUMBER_OF_ARGUMENTS_MESSAGE);
        }
        if (tableList.get(args[1]) == null) {
            System.out.println(args[1] + " not exists");
        } else {
            if (currentTable != null) {
                currentTable.exit();
            }
            currentTable = new Table(args[1], path);
            System.out.println("using " + args[1]);
        }
    }

    public static void showtables(final String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Invalid command");
        }
        if (!args[1].equals("tables")) {
            throw new IllegalArgumentException("Invalid command");
        }
        for (Map.Entry<String, Integer> i : tableList.entrySet()) {
            String key = i.getKey();
            Integer num = i.getValue();
            System.out.println(key + " " + num.toString());
        }
    }

    public static void exit(final String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("exit: " +
                    INVALID_NUMBER_OF_ARGUMENTS_MESSAGE);
        }
        if (currentTable != null) {
            currentTable.exit();
        }
        System.exit(0);
    }
}


