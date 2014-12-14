package ru.fizteh.fivt.students.egor_belikov.Parallel;

import com.google.common.base.Joiner;
import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static ru.fizteh.fivt.students.egor_belikov.Parallel.MyTableProvider.listOfTables;
import ru.fizteh.fivt.students.egor_belikov.Parallel.Commands.*;

public class ParallelMain {
    public static String currentPath;
    public static boolean isInteractiveMode;
    public static MyTableProvider myTableProvider;


    public static String currentTable;
    private static TreeMap<String, Pair<Boolean, Integer>> listOfCommandsDescription;
    private static TreeMap<String, Command> listOfCommands;


    public static void main(String[] args) {
        currentPath = System.getProperty("fizteh.db.dir");//System.getProperty("user.dir") + File.separator + "db";
        initListsOfCommands();
        MyTableProviderFactory myTableProviderFactory = new MyTableProviderFactory();
        try {
            myTableProvider = (MyTableProvider) myTableProviderFactory.create(currentPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (currentPath == null) {
            throw new IllegalArgumentException("Parallel.main: null path");
        }
        try {
            File directoryOnPath = new File(currentPath);
            if (!directoryOnPath.exists() || !directoryOnPath.isDirectory()) {
                throw new Exception("Parallel.main: directory does not exists");
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

    private static void initListsOfCommands() {
        listOfCommandsDescription = new TreeMap<>();
        listOfCommandsDescription.put("create", new Pair<>(false, 2));
        listOfCommandsDescription.put("drop", new Pair<>(false, 2));
        listOfCommandsDescription.put("put", new Pair<>(true, 2));
        listOfCommandsDescription.put("get", new Pair<>(true, 3));
        listOfCommandsDescription.put("use", new Pair<>(false, 2));
        listOfCommandsDescription.put("show", new Pair<>(false, 2));
        listOfCommandsDescription.put("remove", new Pair<>(true, 2));
        listOfCommandsDescription.put("list", new Pair<>(true, 1));
        listOfCommandsDescription.put("commit", new Pair<>(true, 1));
        listOfCommandsDescription.put("rollback", new Pair<>(true, 1));
        listOfCommandsDescription.put("size", new Pair<>(true, 1));
        listOfCommandsDescription.put("exit", new Pair<>(false, 1));

        listOfCommands = new TreeMap<>();
        listOfCommands.put("create", new Create());
        listOfCommands.put("drop", new Drop());
        listOfCommands.put("put", new Put());
        listOfCommands.put("get", new Get());
        listOfCommands.put("use", new Use());
        listOfCommands.put("show", new Show());
        listOfCommands.put("remove", new Remove());
        listOfCommands.put("list", new ListCommand());
        listOfCommands.put("commit", new Commit());
        listOfCommands.put("rollback", new Rollback());
        listOfCommands.put("size", new Size());
        listOfCommands.put("exit", new Exit());


    }

    public static void pack(String[] args) throws Exception {
        String commands = Joiner.on(" ").join(args);
        String[] splittedCommands = commands.trim().split(";");
        try {
            for (String s : splittedCommands) {
                execute(s);
            }
        } catch (Exception exception) {
            throw new Exception("ParallelMain: some exception in pack method");
        }
    }

    public static void interactive() {
        Scanner scanner = new Scanner(System.in);
        try {
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
            if (listOfCommands.containsKey(args[0])) {
                if (listOfCommandsDescription.get(args[0]).getKey() && currentTable == null) {
                    throw new Exception("no table");
                }
                if (!listOfCommandsDescription.get(args[0]).getValue().equals(args.length)) {
                    throw new Exception(args[0] + ": invalid number of arguments");
                }
                listOfCommands.get(args[0]).execute(args, myTableProvider);
            } else {
                throw new Exception("Invalid command");
            }
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            if (!isInteractiveMode) {
                System.exit(1);
            }
        }
    }
}
