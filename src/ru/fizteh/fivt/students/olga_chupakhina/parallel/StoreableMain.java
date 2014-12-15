package ru.fizteh.fivt.students.olga_chupakhina.parallel;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static ru.fizteh.fivt.students.olga_chupakhina.storeable.Serializer.stringToClass;

public class StoreableMain {
    public static boolean mode;
    public static String path;
    public static OTableProvider tableProvider;

    public static void main(final String[] args) {
        try {
            path = System.getProperty("fizteh.db.dir");
            OTableProviderFactory tableProviderFactory = new OTableProviderFactory();
            tableProvider = (OTableProvider) tableProviderFactory.create(path);
            if (path == null) {
                throw new Exception("Enter directory");
            }
            File dir = new File(path);
            if (!dir.exists() || !dir.isDirectory()) {
                throw new Exception("directory not exist");
            }
            File[] children = dir.listFiles();
            for (File child : children) {
                OTable table = new OTable(child.getName(), path, null);
                table.load();
                tableProvider.tableList.put(child.getName(), table);
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
                    if (tableProvider.currentTable == null) {
                        throw new Exception("no table");
                    }
                    put(args);

                } else if (args[0].equals("get")) {
                    if (tableProvider.currentTable == null) {
                        throw new Exception("no table");
                    }
                    get(args);

                } else if (args[0].equals("remove")) {
                    if (tableProvider.currentTable == null) {
                        throw new Exception("no table");
                    }
                    remove(args);

                } else if (args[0].equals("list")) {
                    if (tableProvider.currentTable == null) {
                        throw new Exception("no table");
                    }
                    list(args);

                } else if (args[0].equals("commit")) {
                    if (tableProvider.currentTable == null) {
                        throw new Exception("no table");
                    } else {
                        commit(args);
                    }
                } else if (args[0].equals("rollback")) {
                    if (tableProvider.currentTable == null) {
                        throw new Exception("no table");
                    } else {
                        rollback(args);
                    }
                } else if (args[0].equals("size")) {
                    if (tableProvider.currentTable == null) {
                        throw new Exception("no table");
                    } else {
                        size(args);
                    }
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
        String storeable = args[2];
        if (args.length > 3) {
            for (int i = 3; i != args.length; i++) {
                storeable = storeable + " " + args[i];
            }
        }
        Storeable value = tableProvider.deserialize(tableProvider.currentTable, storeable);
        if (tableProvider.currentTable.put(args[1], value) == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
        }
    }

    public static void get(final String[] args) throws Exception {
        checkNumOfArgs("get", 2, args.length);
        if (tableProvider.currentTable.get(args[1]) == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(tableProvider.serialize(tableProvider.currentTable, 
            tableProvider.currentTable.get(args[1])));
        }
    }

    public static void remove(final String[] args) throws Exception {
        checkNumOfArgs("remove", 2, args.length);
        if (tableProvider.currentTable.remove(args[1]) == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }

    public static void create(String[] args) throws Exception {
        String typesString = "";
        for (int i = 2; i != args.length; i++) {
            typesString = typesString + " " + args[i];
        }
        typesString = typesString.trim();
        if (typesString.length() < 3
                || typesString.charAt(0) != '('
                || typesString.charAt(typesString.length() - 1) != ')') {
            throw new IllegalArgumentException("wrong types (signature)");
        }
        List<Class<?>> signature = new ArrayList<Class<?>>();
        String[] types = typesString.substring(1, typesString.length() - 1).split("\\s+");
        for (String type : types) {
            if (type.trim().isEmpty()) {
                throw new Exception("wrong types (signature)");
            }
            Class<?> c = stringToClass(type.trim());
            if (c == null) {
                throw new Exception("wrong type (" + type.trim() + " is not a valid type name)");
            }
            signature.add(c);
        }
        if (types.length == 0) {
            throw new Exception("wrong type (empty type is not allowed)");
        }
        OTable t = (OTable) tableProvider.createTable(args[1], signature);
        t.writeSignature();
        if (t != null) {
            System.out.println("created");
            tableProvider.tableList.put(args[1], t);
        } else {
            System.out.println(args[1] + " exists");
        }
    }

    public static void drop(final String[] args) throws Exception {
        checkNumOfArgs("drop", 2, args.length);
        try {
            tableProvider.removeTable(args[1]);
            System.out.println("dropped");
        } catch (IllegalStateException ist) {
            System.out.println(args[1] + " not exists");
        }
    }

    public static void use(String[] args) throws Exception {
        checkNumOfArgs("use", 2, args.length);
        if (tableProvider.tableList.get(args[1]) == null) {
            System.out.println(args[1] + " not exists");
        } else {
            if (tableProvider.currentTable != null && tableProvider.currentTable.unsavedChangesCounter > 0) {
                System.out.println(tableProvider.currentTable.unsavedChangesCounter + " unsaved changes");
            } else {
                tableProvider.currentTable = (OTable) tableProvider.tableList.get(args[1]);
                System.out.println("using " + args[1]);
            }
        }
    }

    public static void commit(String[] args) throws Exception {
        checkNumOfArgs("commit", 1, args.length);
        System.out.println(tableProvider.currentTable.commit());
    }

    public static void rollback(String[] args) throws Exception {
        checkNumOfArgs("rollback", 1, args.length);
        System.out.println(tableProvider.currentTable.rollback());
    }

    public static void size(String[] args) throws Exception {
        checkNumOfArgs("size", 1, args.length);
        System.out.println(tableProvider.currentTable.numberOfElements);
    }

    public static void list(final String[] args) throws Exception {
        checkNumOfArgs("list", 1, args.length);
        int counter = 0;
        List<String> list = tableProvider.currentTable.list();
        for (String current : list) {
            ++counter;
            System.out.print(current);
            if (counter != list.size()) {
                System.out.print(", ");
            }
        }
        System.out.println();
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
        checkNumOfArgs("show tables", 2, args.length);
        System.out.println("table_name row_count");
        for (Map.Entry<String, Table> i :  tableProvider.tableList.entrySet()) {
            String key = i.getKey();
            int num = ((OTable) (i.getValue())).numberOfElements;
            System.out.println(key + " " + num);
        }
    }

    public static void exit(final String[] args) throws Exception {
        checkNumOfArgs("exit", 1, args.length);
        System.exit(0);
    }

    public static void checkNumOfArgs(String operation,
                                      int correctValue,
                                      int testValue) throws Exception {
        if (testValue != correctValue) {
            throw new Exception(operation
                    + ": Invalid number of arguments");
        }
    }
}
