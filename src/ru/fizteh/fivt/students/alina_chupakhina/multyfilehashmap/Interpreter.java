package ru.fizteh.fivt.students.alina_chupakhina.multyfilehashmap;

import java.io.File;
import java.util.Map;

public class Interpreter {
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
                if (MultiFileHashMap.currentTable == null) {
                    System.out.println("no table");
                } else {
                    put(args);
                }
            } else if (args[0].equals("get")) {
                if (MultiFileHashMap.currentTable == null) {
                    System.out.println("no table");
                } else {
                    MultiFileHashMap.currentTable.get(args);
                }
            } else if (args[0].equals("remove")) {
                if (MultiFileHashMap.currentTable == null) {
                    System.out.println("no table");
                } else {
                    remove(args);
                }
            } else if (args[0].equals("list")) {
                if (MultiFileHashMap.currentTable == null) {
                    System.out.println("no table");
                } else {
                    MultiFileHashMap.currentTable.list(args);
                }
            } else if (args[0].equals("exit")) {
                exit(args);
            } else if (args[0].equals("")) {
                //
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
        MultiFileHashMap.currentTable.put(args);
        MultiFileHashMap.tableList.put(MultiFileHashMap.currentTable.getName(),
                MultiFileHashMap.currentTable.getNumberOfElements());
    }

    public static void remove(final String[] args) throws Exception {
        MultiFileHashMap.currentTable.remove(args);
        MultiFileHashMap.tableList.put(MultiFileHashMap.currentTable.getName(),
                MultiFileHashMap.currentTable.getNumberOfElements());
    }

    public static void create(String[] args) throws Exception {
        checkNumOfArgs("create", 2, args.length);
        String pathToTable = MultiFileHashMap.path + File.separator + args[1];
        File table = new File(pathToTable);
        if (table.exists() && table.isDirectory()) {
            System.out.println(args[1] + " exists");
        } else {
            table.mkdir();
            Table t = new Table(args[1], MultiFileHashMap.path);
            MultiFileHashMap.tableList.put(args[1], 0);
            System.out.println("created");
        }
    }

    public static void drop(final String[] args) {
        checkNumOfArgs("drop", 2, args.length);
        String pathToTable = MultiFileHashMap.path + File.separator + args[1];
        File table = new File(pathToTable);
        if (!table.exists() || !table.isDirectory()) {
            System.out.println(args[1] + " not exists");
        } else {
            if (MultiFileHashMap.currentTable.getName().equals(args[1])) {
                MultiFileHashMap.currentTable = null;
            }
            MultiFileHashMap.tableList.remove(args[1]);
            table.delete();
            System.out.println("dropped");
        }
    }

    public static void use(String[] args) throws Exception {
        checkNumOfArgs("use", 2, args.length);
        if (MultiFileHashMap.tableList.get(args[1]) == null) {
            System.out.println(args[1] + " not exists");
        } else {
            if (MultiFileHashMap.currentTable != null) {
                MultiFileHashMap.currentTable.exit();
            }
            MultiFileHashMap.currentTable = new Table(args[1], MultiFileHashMap.path);
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
        System.out.println("table_name row_count");
        for (Map.Entry<String, Integer> i : MultiFileHashMap.tableList.entrySet()) {
            String key = i.getKey();
            Integer num = i.getValue();
            System.out.println(key + " " + num.toString());
        }
    }

    public static void exit(final String[] args) throws Exception {
        checkNumOfArgs("exit", 1, args.length);
        if (MultiFileHashMap.currentTable != null) {
            MultiFileHashMap.currentTable.exit();
        }
        System.exit(0);
    }

    public static void checkNumOfArgs(String operation,
                                      int correctValue,
                                      int testValue) throws IllegalArgumentException {
        if (testValue != correctValue) {
            throw new IllegalArgumentException(operation
                    + ": Invalid number of arguments");
        }
    }
}

