package ru.fizteh.fivt.students.olga_chupakhina.multyfilehashmap;

import java.util.*;
import java.io.*;


public class MultiFileHashMap {
    private static String path;
    private static boolean mode;
    private static Table curTable;
    private static TreeMap<String, Integer> tableList;

    public static void main(String[] args)
            throws Exception {
        try {
            path = System.getProperty("fizteh.db.dir");
            //path = "D:\\tables";
            if (path == null) {
                throw new Exception("Enter directory");
            }
            tableList = new TreeMap<>();
            File dir = new File(path);
            if (!dir.exists() || !dir.isDirectory()) {
                throw new Exception("directory not exist");
            }
            File[] children = dir.listFiles();
            for (File child : children) {
                Table t = new Table(child.getName(), path);
                tableList.put(child.getName(), t.getNumberOfElements());
                t.exit();
            }
            if (args.length == 0) {
                mode = true;
                interactiveMode();
            } else {
                mode = false;
                packageMode(args);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    private static void packageMode(String[] args) {
        StringBuilder commands = new StringBuilder();
        for (String arg: args) {
            commands.append(arg);
            commands.append(' ');
        }
        separationLine(commands.toString());
    }

    private static void interactiveMode() throws Exception {
        Scanner scanner = new Scanner(System.in);
        try  {
            while (true) {
                System.out.print("$ ");
                String commands = scanner.nextLine();
                separationLine(commands);
            }
        } catch (NoSuchElementException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    private static void separationLine(String line) {
        String[] commands = line.trim().split(";");
        try {
            for (int i = 0; i < commands.length; i++) {
                doCommand(commands[i]);
            }
        } catch (Exception e) {
            System.exit(0);
        }
    }

    private static void doCommand(String command)
            throws Exception {
        command = command.trim();
        String[] args = command.split("\\s+");
        boolean done = false;
        try {
            if (args.length > 0 && !args[0].isEmpty()) {

                if (args[0].equals("use")) {
                    use(args);

                } else if (args[0].equals("create")) {
                    create(args);

                } else if (args[0].equals("drop")) {
                    drop(args);

                } else if (args[0].equals("show")) {
                    show(args);

                } else if (args[0].equals("put")) {
                    if (curTable == null) {
                        throw new Exception("no table");
                    }
                    put(args);

                } else if (args[0].equals("get")) {
                    if (curTable == null) {
                        throw new Exception("no table");
                    }
                    curTable.get(args);

                } else if (args[0].equals("remove")) {
                    if (curTable == null) {
                        throw new Exception("no table");
                    }
                    remove(args);

                } else if (args[0].equals("list")) {
                    if (curTable == null) {
                        throw new Exception("no table");
                    }
                    curTable.list(args);

                } else if (args[0].equals("exit")) {
                    exit(args);
                } else {
                    throw new Exception(args[0] + ": Invalid command");
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            if (mode) {
                System.exit(-1);
            }
        }
    }

    public static void create(String[] args) throws Exception {
        if (args.length != 2) {
            throw new Exception("create: invalid number of arguments");
        }
        File file = new File(path + File.separator + args[1] + File.separator);
        if (file.exists()) {
            System.out.println(args[1] + " exists");
        } else {
            file.mkdir();
            tableList.put(args[1], 0);
            System.out.println("created");
        }
    }

    public static void drop(String[] args) throws Exception {
        if (args.length != 2) {
            throw new Exception("create: invalid number of arguments");
        }
        String pathToTable = MultiFileHashMap.path + File.separator + args[1];
        File table = new File(pathToTable);
        if (!table.exists() || !table.isDirectory()) {
            System.out.println(args[1] + " not exists");
        } else {
            if (curTable != null) {
                if (curTable.getName().equals(args[1])) {
                    curTable = null;
                }
            }
            tableList.remove(args[1]);
            Table t = new Table(args[1], MultiFileHashMap.path);
            t.rm();
            table.delete();
            System.out.println("dropped");
        }
    }

    public static void use(String[] args) throws Exception {
        if (args.length != 2) {
            throw new Exception("create: invalid number of arguments");
        }
        if (MultiFileHashMap.tableList.get(args[1]) == null) {
            System.out.println(args[1] + " not exists");
        } else {
            if (curTable != null) {
                curTable.exit();
            }
            curTable = new Table(args[1], MultiFileHashMap.path);
            System.out.println("using " + args[1]);
        }
    }

    public  static void show(String[] args) throws Exception {
        System.out.println("table_name row_count");
        for (Map.Entry<String, Integer> i : MultiFileHashMap.tableList.entrySet()) {
            String key = i.getKey();
            Integer num = i.getValue();
            System.out.println(key + " " + num.toString());
        }
    }
    public static void put(final String[] args) throws Exception {
        curTable.put(args);
        MultiFileHashMap.tableList.put(curTable.getName(),
                curTable.getNumberOfElements());
    }

    public static void remove(final String[] args) throws Exception {
        curTable.remove(args);
        MultiFileHashMap.tableList.put(curTable.getName(),
                curTable.getNumberOfElements());
    }

    public static void exit(String[] args) throws Exception {
        if (args.length != 1) {
            throw new Exception("exit: invalid number of arguments");
        }
        if (curTable != null) {
            curTable.exit();
        }
        System.exit(0);
    }
}

