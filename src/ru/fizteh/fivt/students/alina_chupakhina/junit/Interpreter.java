package ru.fizteh.fivt.students.alina_chupakhina.junit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;

public class Interpreter {
    public static TableProvider pv = JUnit.pv;
    public PrintStream out;
    public static final String MESSAGE_INVALID_COMMAND = " - invalid command";
    public static final String MESSAGE_INVALID_NUMBER_OF_ARGUMENTS = ": Invalid number of arguments";
    public Interpreter(final PrintStream outStream) {
        out = outStream;
    }
    public void doCommand(final String command)
            throws Exception {
        String[] args = command.trim().split("\\s+");
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
                out.println("no table");
            } else {
                put(args);
            }
        } else if (args[0].equals("get")) {
            if (JUnit.currentTable == null) {
                out.println("no table");
            } else {
                get(args);
            }
        } else if (args[0].equals("remove")) {
            if (JUnit.currentTable == null) {
                out.println("no table");
            } else {
                remove(args);
            }
        } else if (args[0].equals("list")) {
            if (JUnit.currentTable == null) {
                out.println("no table");
            } else {
                list(args);
            }
        } else if (args[0].equals("commit")) {
            if (JUnit.currentTable == null) {
                out.println("no table");
            } else {
                commit(args);
            }
        } else if (args[0].equals("rollback")) {
            if (JUnit.currentTable == null) {
                out.println("no table");
            } else {
                rollback(args);
            }
        } else if (args[0].equals("size")) {
            if (JUnit.currentTable == null) {
                out.println("no table");
            } else {
                size(args);
            }
        } else if (args[0].equals("exit")) {
            exit(args);
        } else if (args[0].equals("")) {
            //Nothing
        } else {
            throw new IllegalArgumentException(args[0] + MESSAGE_INVALID_COMMAND);
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
        Table t = pv.createTable(args[1]);
        if (t != null) {
            System.out.println("created");
            JUnit.tableList.put(args[1], t);
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
            if (JUnit.currentTable != null && JUnit.currentTable.unsavedChangesCounter > 0) {
                System.out.println(JUnit.currentTable.unsavedChangesCounter + " unsaved changes");
            } else {
                JUnit.currentTable = (BdTable) JUnit.tableList.get(args[1]);
                System.out.println("using " + args[1]);
            }
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

    public static void showtables(final String[] args) throws Exception {
        if (args.length >= 2) {
            if (!args[1].equals("tables")) {
                throw new Exception("Invalid command");
            }
        }
        if (args.length == 1) {
            throw new Exception("Invalid command");
        }
        checkNumOfArgs("show tables", 2, args.length);
        System.out.println("table_name row_count");
        for (Map.Entry<String, Table> i : JUnit.tableList.entrySet()) {
            String key = i.getKey();
            Integer num = ((BdTable) (i.getValue())).numberOfElements;
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
                    + MESSAGE_INVALID_NUMBER_OF_ARGUMENTS);
        }
    }
}
