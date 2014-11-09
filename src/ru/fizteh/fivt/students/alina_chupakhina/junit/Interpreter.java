package ru.fizteh.fivt.students.alina_chupakhina.junit;

import java.util.List;
import java.util.Map;

public class Interpreter {
    public static PvTable pv = JUnit.pv;
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
                if (JUnit.currentTable == null) {
                    System.err.println("no table");
                } else {
                    put(args);
                }
            } else if (args[0].equals("get")) {
                if (JUnit.currentTable == null) {
                    System.err.println("no table");
                } else {
                    get(args);
                }
            } else if (args[0].equals("remove")) {
                if (JUnit.currentTable == null) {
                    System.err.println("no table");
                } else {
                    remove(args);
                }
            } else if (args[0].equals("list")) {
                if (JUnit.currentTable == null) {
                    System.err.println("no table");
                } else {
                    list(args);
                }
            } else if (args[0].equals("commit")) {
                if (JUnit.currentTable == null) {
                    System.err.println("no table");
                } else {
                    commit(args);
                }
            } else if (args[0].equals("rollback")) {
                if (JUnit.currentTable == null) {
                    System.err.println("no table");
                } else {
                    rollback(args);
                }
            }else if (args[0].equals("size")) {
                if (JUnit.currentTable == null) {
                    System.err.println("no table");
                } else {
                    size(args);
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
        checkNumOfArgs("put", 3, args.length);
        if (JUnit.currentTable.put(args[1], args[2]) == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
        }
    }

    public static void get(final String[] args) throws Exception {
        checkNumOfArgs("get", 2, args.length);
        if (JUnit.currentTable.get(args[1]) == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(JUnit.currentTable.get(args[1]));
        }
    }

    public static void list(final String[] args) throws Exception {
        checkNumOfArgs("list", 1, args.length);
        int counter = 0;
        List<String> list = JUnit.currentTable.list();
        for (String current : list) {
            ++counter;
            System.out.print(current);
            if (counter != list.size()) {
                System.out.print(", ");
            }
        }
        System.out.println();
    }

    public static void remove(final String[] args) throws Exception {
        checkNumOfArgs("remove", 2, args.length);
        if (JUnit.currentTable.remove(args[1]) == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }

    public static void create(String[] args) throws Exception {
        checkNumOfArgs("create", 2, args.length);
        if (pv.createTable(args[1]) != null) {
            System.out.println("created");
        } else {
            System.out.println(args[1] + " exists");
        }

    }

    public static void drop(final String[] args) throws Exception {
        checkNumOfArgs("drop", 2, args.length);
        try {
            pv.removeTable(args[1]);
            System.out.println("dropped");
        } catch (IllegalStateException ist) {
            System.out.println(args[1] + " not exists");
        }
    }

    public static void use(String[] args) throws Exception {
        checkNumOfArgs("use", 2, args.length);
        if (JUnit.tableList.get(args[1]) == null) {
            System.out.println(args[1] + " not exists");
        } else {
            if (JUnit.currentTable != null) {
                throw new Exception(JUnit.currentTable.unsavedChangesCounter + " unsaved changes");
            }
            JUnit.currentTable = JUnit.tableList.get(args[1]);
            System.out.println("using " + args[1]);
        }
    }

    public static void commit(String[] args) throws Exception {
        checkNumOfArgs("commit", 1, args.length);
        System.out.println(JUnit.currentTable.commit());
    }

    public static void rollback(String[] args) throws Exception {
        checkNumOfArgs("rollback", 1, args.length);
        System.out.println(JUnit.currentTable.rollback());
    }

    public static void size(String[] args) throws Exception {
        checkNumOfArgs("rollback", 1, args.length);
        System.out.println(JUnit.currentTable.numberOfElements);
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
        for (Map.Entry<String, BdTable> i : JUnit.tableList.entrySet()) {
            String key = i.getKey();
            Integer num = (i.getValue()).numberOfElements;
            System.out.println(key + " " + num.toString());
        }
    }

    public static void exit(final String[] args) throws Exception {
        checkNumOfArgs("exit", 1, args.length);
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
