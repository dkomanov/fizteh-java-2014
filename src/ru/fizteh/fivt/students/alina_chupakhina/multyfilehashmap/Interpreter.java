package ru.fizteh.fivt.students.alina_chupakhina.multyfilehashmap;

import java.io.File;
import java.util.Map;

public class Interpreter {
    public static Table currentTable;
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
                    System.err.println("no table");
                } else {
                    put(args);
                }
            } else if (args[0].equals("get")) {
                if (currentTable == null) {
                    System.err.println("no table");
                } else {
                    currentTable.get(args);
                }
            } else if (args[0].equals("remove")) {
                if (currentTable == null) {
                    System.err.println("no table");
                } else {
                    remove(args);
                }
            } else if (args[0].equals("list")) {
                if (currentTable == null) {
                    System.err.println("no table");
                } else {
                    currentTable.list(args);
                }
            } else if (args[0].equals("exit")) {
                exit(args);
            } else if (args[0].equals("")) {
                //Nothing
            } else {
                throw new Exception(args[0] + " - invalid command");
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
        MultiFileHashMap.tableList.put(currentTable.getName(),
                currentTable.getNumberOfElements());
    }

    public static void remove(final String[] args) throws Exception {
        currentTable.remove(args);
        MultiFileHashMap.tableList.put(currentTable.getName(),
                currentTable.getNumberOfElements());
    }

    public static void create(String[] args) throws Exception {
        checkNumOfArgs("create", 2, args.length);
        String pathToTable = MultiFileHashMap.path + File.separator + args[1];
        File table = new File(pathToTable);
        if (table.exists() && table.isDirectory()) {
            System.out.println(args[1] + " exists");
        } else {
            table.mkdir();
            MultiFileHashMap.tableList.put(args[1], 0);
            System.out.println("created");
        }
    }

    public static void drop(final String[] args) throws Exception {
        checkNumOfArgs("drop", 2, args.length);
        String pathToTable = MultiFileHashMap.path + File.separator + args[1];
        File table = new File(pathToTable);
        if (!table.exists() || !table.isDirectory()) {
            System.out.println(args[1] + " not exists");
        } else {
            if (currentTable != null) {
                if (currentTable.getName().equals(args[1])) {
                    currentTable = null;
                }
            }
            MultiFileHashMap.tableList.remove(args[1]);
            Table t = new Table(args[1], MultiFileHashMap.path);
            t.rm();
            table.delete();
            System.out.println("dropped");
        }
    }

    public static void use(String[] args) throws Exception {
        checkNumOfArgs("use", 2, args.length);
        if (MultiFileHashMap.tableList.get(args[1]) == null) {
            System.out.println(args[1] + " not exists");
        } else {
            if (currentTable != null) {
                currentTable.exit();
            }
            currentTable = new Table(args[1], MultiFileHashMap.path);
            System.out.println("using " + args[1]);
        }
    }

    public static void showtables(final String[] args) {
        if (args.length >= 2) {
            if (!args[1].equals("tables")) {
                throw new IllegalArgumentException("Invalid command");
            }
        }
        if (args.length == 1) {
            throw new IllegalArgumentException("Invalid command");
        }
        checkNumOfArgs("show tables", 2, args.length);
        System.out.println("table_name row_count");
        for (Map.Entry<String, Integer> i : MultiFileHashMap.tableList.entrySet()) {
            String key = i.getKey();
            Integer num = i.getValue();
            System.out.println(key + " " + num.toString());
        }
    }

    public static void exit(final String[] args) throws Exception {
        checkNumOfArgs("exit", 1, args.length);
        if (currentTable != null) {
            currentTable.exit();
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




