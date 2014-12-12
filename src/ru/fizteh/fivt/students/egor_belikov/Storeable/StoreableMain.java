package ru.fizteh.fivt.students.egor_belikov.Storeable;

import java.io.*;
import java.text.ParseException;
import java.util.*;

import com.google.common.base.Joiner;
import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.storage.structured.Table;

import static ru.fizteh.fivt.students.egor_belikov.Storeable.MySerializer.returningClass;
import static ru.fizteh.fivt.students.egor_belikov.Storeable.MyTableProvider.*;

public class StoreableMain {
    public static String currentPath;
    public static boolean isInteractiveMode;
    public static MyTableProvider myTableProvider;


    public static String currentTable;
    static String separator;


    public static void main(String[] args) throws Exception {
        separator = File.separator;
        currentPath = System.getProperty("fizteh.db.dir");

        MyTableProviderFactory myTableProviderFactory = new MyTableProviderFactory();
        myTableProvider = (MyTableProvider) myTableProviderFactory.create(currentPath);
        if (currentPath == null) {
            throw new IllegalArgumentException("Storeable.main: null path");
        }
        try {
            File directoryOnPath = new File(currentPath);
            if (!directoryOnPath.exists() || !directoryOnPath.isDirectory()) {
                throw new Exception("Storeable.main: directory does not exists");
            }
            File[] children = directoryOnPath.listFiles();
            for (File child : Objects.requireNonNull(children)) {
                MyTable tempTable = new MyTable(child.getName(), currentPath, null);
                tempTable.load();
                listOfTables.put(child.getName(), tempTable);
            }
            isInteractiveMode = (args.length == 0);
            if (isInteractiveMode) {
                interactive();
            } else {
                pack(args);
            }
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            System.exit(1);
        }
    }

    public static void pack(String[] args) {
        String commands = Joiner.on(" ").join(args);
        String[] splittedCommands = commands.trim().split(";");
        try {
            for (String s: splittedCommands) {
                execute(s);
            }
        } catch (Exception exception) {
            System.exit(0);
        }
    }

    public static void interactive() {
        Scanner scanner = new Scanner(System.in);
        try  {
            while (true) {
                System.out.print("$ ");
                String commands;
                if (scanner.hasNextLine()) {
                    commands = scanner.nextLine();
                    try {
                        String[] splittedCommands = commands.trim().split(";");
                        for (String s : splittedCommands) {
                            execute(s);
                        }
                    } catch (Exception exception) {
                        System.out.println(exception.getMessage());
                        System.exit(0);
                    }
                }
            }
        } catch (NoSuchElementException exception) {
            System.err.println(exception.getMessage());
            System.exit(1);
        }
    }

    public static void execute(String s) throws Exception {
        try {
            String[] args = s.trim().split("\\s+");
            if (args[0].equals("get") || args[0].equals("remove") || args[0].equals("list") || args[0].equals("put")
                    || args[0].equals("commit") || args[0].equals("rollback")) {
                if (currentTable == null) {
                    throw new Exception("no table");
                }
            }
            if (args[0].equals("drop") || args[0].equals("use")
                    || args[0].equals("get") || args[0].equals("remove")
                    || args[0].equals("show")) {
                if (args.length != 2) {
                    throw new Exception(args[0] + ": invalid number of arguments");
                }
            }
            if (args[0].equals("list") || args[0].equals("exit") || args[0].equals("commit")
                    || args[0].equals("rollback") || args[0].equals("size")) {
                if (args.length != 1) {
                    throw new Exception(args[0] + ": invalid number of arguments");
                }
            }

            switch (args[0]) {
                case "create":
                    create(args);
                    break;
                case "drop":
                    drop(args);
                    break;
                case "use":
                    use(args);
                    break;
                case "show":
                    show();
                    break;
                case "put":
                    put(args);
                    break;
                case "get":
                    get(args);
                    break;
                case "remove":
                    remove(args);
                    break;
                case "list":
                    list();
                    break;
                case "commit":
                    commit();
                    break;
                case "rollback":
                    rollback();
                    break;
                case "size":
                    size();
                    break;
                case "exit":
                    System.exit(0);
                    break;
                default:
                    throw new Exception("Invalid command");
            }
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            if (!isInteractiveMode) {
                System.exit(1);
            }
        }
    }

    public static void create(String[] args) throws Exception {
        String stringTypes = "";
        for (int i = 2; i != args.length; i++) {
            stringTypes = stringTypes + " " + args[i];
        }
        stringTypes = stringTypes.trim();
        if (stringTypes.length() < 3
                || stringTypes.charAt(0) != '('
                || stringTypes.charAt(stringTypes.length() - 1) != ')') {
            throw new IllegalArgumentException("wrong types (signature)");
        }
        List<Class<?>> signature = new ArrayList<>();
        String[] types = stringTypes.substring(1, stringTypes.length() - 1).split("\\s+");
        for (String type : types) {
            if (type.trim().isEmpty()) {
                throw new Exception("wrong types (signature)");
            }
            Class<?> c = returningClass(type.trim());
            if (c == null) {
                throw new Exception("wrong type (" + type.trim() + " is not a valid type name)");
            }
            signature.add(c);
        }
        if (types.length == 0) {
            throw new Exception("wrong type (empty type is not allowed)");
        }
        MyTable t = (MyTable) myTableProvider.createTable(args[1], signature);
        t.writeSignature();
        if (t != null) {
            System.out.println("created");
            listOfTables.put(args[1], t);
        } else {
            System.out.println(args[1] + " exists");
        }
    }

    public static void drop(String[] args) {
        try {
            myTableProvider.removeTable(args[1]);
            System.out.println("dropped");
        } catch (IllegalStateException ist) {
            System.out.println(args[1] + " does not exists");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void use(String[] args) {
        if (listOfTables.get(args[1]) == null) {
            System.out.println(args[1] + " not exists");
        } else {
            if (MyTableProvider.currentTable != null && MyTableProvider.currentTable.numberOfUnsavedChanges > 0) {
                System.out.println(MyTableProvider.currentTable.numberOfUnsavedChanges + " unsaved changes");
            } else {
                MyTableProvider.currentTable = (MyTable) listOfTables.get(args[1]);
                System.out.println("using " + args[1]);
            }
        }
    }

    public static void show() {
        System.out.println("table_name row_count");
        for (Map.Entry<String, Table> i :  listOfTables.entrySet()) {
            String key = i.getKey();
            int num = ((MyTable) (i.getValue())).numberOfElements;
            System.out.println(key + " " + num);
        }
    }

    public static void put(String[] args) {
        String arguments = args[2];
        if (args.length > 3) {
            for (int i = 3; i != args.length; i++) {
                arguments = arguments + " " + args[i];
            }
        }
        Storeable value = null;
        try {
            value = myTableProvider.deserialize(MyTableProvider.currentTable, arguments);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (MyTableProvider.currentTable.put(args[1], value) == null) {
            System.out.println("new");
        } else {
            System.out.println("overwrite");
        }
    }

    public static void get(String[] args) {
        if (MyTableProvider.currentTable.get(args[1]) == null) {
            System.out.println("not found");
        } else {
            System.out.println("found");
            System.out.println(myTableProvider.serialize(MyTableProvider.currentTable,
                    MyTableProvider.currentTable.get(args[1])));
        }
    }

    public static void remove(String[] args) {
        if (MyTableProvider.currentTable.remove(args[1]) == null) {
            System.out.println("not found");
        } else {
            System.out.println("removed");
        }
    }

    public static void list() {
        int counter = 0;
        List<String> list = MyTableProvider.currentTable.list();
        for (String current : list) {
            ++counter;
            System.out.print(current);
            if (counter != list.size()) {
                System.out.print(", ");
            }
        }
        System.out.println();
    }

    public static void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                File f = new File(dir, aChildren);
                deleteDirectory(f);
            }
        }
        dir.delete();
    }

    public static void size() {
        System.out.println(MyTableProvider.currentTable.numberOfElements);
    }

    public static void commit() throws Exception {
        System.out.println(MyTableProvider.currentTable.commit());

    }

    public static void rollback() {
        System.out.println(MyTableProvider.currentTable.rollback());

    }
}
