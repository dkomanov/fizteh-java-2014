package ru.fizteh.fivt.students.olga_chupakhina.junit;

import ru.fizteh.fivt.storage.strings.Table;
import ru.fizteh.fivt.storage.strings.TableProvider;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class JUnit {
    public static boolean mode;
    public static String path;
    public static Map<String, Table> tableList;
    public static OTable currentTable;
    public static TableProvider pv;

    public static void main(final String[] args) {
        try {
            path = System.getProperty("fizteh.db.dir");
            if (path == null) {
                throw new Exception("Enter directory");
            }
            tableList = new TreeMap<String, Table>();
            File dir = new File(path);
            if (!dir.exists() || !dir.isDirectory()) {
                throw new Exception("directory not exist");
            }
            File[] children = dir.listFiles();
            for (File child : children) {
                TablePF pf = new TablePF();
                pv = pf.create(path);
                tableList.put(child.getName(), pv.getTable(child.getName()));
            }
            if (args.length > 0) {
                mode = true;
                batch(args);
            } else {
                mode = false;
                interactive();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    public static void interactive() throws Exception {
        Scanner sc = new Scanner(System.in);
        try {
            while (true) {
                System.out.print("$ ");
                String [] s = sc.nextLine().trim().split(";");
                for (String command : s) {
                    doCommand(command);
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void batch(final String[] args) throws Exception {
            String arg;
            if (args.length > 0) {
                arg = args[0];
                for (int i = 1; i != args.length; i++) {
                    arg = arg + ' ' + args[i];
                }
                String[] commands = arg.trim().split(";");
                for (int i = 0; i != commands.length; i++) {
                    doCommand(commands[i]);
                }
            }
        }

    public static void doCommand(final String command)
            throws Exception {
        String[] args = command.trim().split("\\s+");
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
                    if (JUnit.currentTable == null) {
                        throw new Exception("no table");
                    }
                    put(args);

                } else if (args[0].equals("get")) {
                    if (JUnit.currentTable == null) {
                        throw new Exception("no table");
                    }
                    get(args);

                } else if (args[0].equals("remove")) {
                    if (JUnit.currentTable == null) {
                        throw new Exception("no table");
                    }
                    remove(args);

                } else if (args[0].equals("list")) {
                    if (JUnit.currentTable == null) {
                        throw new Exception("no table");
                    }
                    list(args);

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

    public static void put(final String[] args) throws Exception {
        checkArgs("put", 3, args.length);
        if (JUnit.currentTable.put(args[1], args[2]) == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
        }
    }

    public static void get(final String[] args) throws Exception {
        checkArgs("get", 2, args.length);
        if (JUnit.currentTable.get(args[1]) == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(JUnit.currentTable.get(args[1]));
        }
    }

    public static void list(final String[] args) throws Exception {
        checkArgs("list", 1, args.length);
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
        checkArgs("remove", 2, args.length);
        if (JUnit.currentTable.remove(args[1]) == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }

    public static void create(String[] args) throws Exception {
        checkArgs("create", 2, args.length);
        Table table = pv.createTable(args[1]);
        if (table != null) {
            System.out.println("created");
            JUnit.tableList.put(args[1], table);
        } else {
            System.out.println(args[1] + " exists");
        }

    }

    public static void drop(final String[] args) throws Exception {
        checkArgs("drop", 2, args.length);
        try {
            pv.removeTable(args[1]);
            System.out.println("dropped");
        } catch (IllegalStateException ist) {
            System.out.println(args[1] + " not exists");
        }
    }

    public static void use(String[] args) throws Exception {
        checkArgs("use", 2, args.length);
        if (JUnit.tableList.get(args[1]) == null) {
            System.out.println(args[1] + " not exists");
        } else {
            if (JUnit.currentTable != null && JUnit.currentTable.unsavedChanges > 0) {
                System.out.println(JUnit.currentTable.unsavedChanges + " unsaved changes");
            } else {
                JUnit.currentTable = (OTable) JUnit.tableList.get(args[1]);
                System.out.println("using " + args[1]);
            }
        }
    }

    public static void commit(String[] args) throws Exception {
        checkArgs("commit", 1, args.length);
        System.out.println(JUnit.currentTable.commit());
    }

    public static void rollback(String[] args) throws Exception {
        checkArgs("rollback", 1, args.length);
        System.out.println(JUnit.currentTable.rollback());
    }

    public static void size(String[] args) throws Exception {
        checkArgs("rollback", 1, args.length);
        System.out.println(JUnit.currentTable.numberOfElements);
    }
    
    public static void show(final String[] args) throws Exception {
        if (args.length >= 2) {
            if (!args[1].equals("tables")) {
                throw new Exception("Invalid command");
            }
        }
        if (args.length == 1) {
            throw new Exception("Invalid command");
        }
        checkArgs("show tables", 2, args.length);
        System.out.println("table_name row_count");
        for (Map.Entry<String, Table> table : JUnit.tableList.entrySet()) {
            Integer num = ((OTable) (table.getValue())).numberOfElements;
            System.out.println(table.getKey() + " " + num.toString());
        }
    }

    public static void exit(final String[] args) throws Exception {
        checkArgs("exit", 1, args.length);
        System.exit(0);
    }

    public static void checkArgs(String operation, int correctValue,
                                 int testValue)
            throws IllegalArgumentException {
        if (testValue != correctValue) {
            throw new IllegalArgumentException(operation
                    + ": Invalid number of arguments");
        }
    }
}
