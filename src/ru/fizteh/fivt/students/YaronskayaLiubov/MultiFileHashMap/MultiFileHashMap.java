package ru.fizteh.fivt.students.YaronskayaLiubov.MultiFileHashMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by luba_yaronskaya on 18.10.14.
 */
public class MultiFileHashMap {
    public static String dbDir;
    public static FileMap currTable;
    protected static HashMap<String, FileMap> tables;
    private static HashMap<String, Command> multiFileHashMapCommands;
    public static boolean errorOccurred;

    public static boolean exec(String[] args) {
        dbDir = System.getProperty("fizteh.db.dir");
        if (dbDir == null) {
            System.err.println("database unspecified in properties");
            return false;
        }
        if (!Files.exists(Paths.get(dbDir))) {
            try {
                Files.createDirectory(Paths.get(dbDir));
            } catch (IOException e) {
                System.err.println("error creating database " + dbDir);
            }
        }

        tables = new HashMap<String, FileMap>();
        multiFileHashMapCommands = new HashMap<String, Command>();
        multiFileHashMapCommands.put("create", new CreateCommand());
        multiFileHashMapCommands.put("drop", new DropCommand());
        multiFileHashMapCommands.put("exit", new ExitCommand());
        multiFileHashMapCommands.put("get", new GetCommand());
        multiFileHashMapCommands.put("list", new ListCommand());
        multiFileHashMapCommands.put("put", new PutCommand());
        multiFileHashMapCommands.put("remove", new RemoveCommand());
        multiFileHashMapCommands.put("show", new ShowCommand());
        multiFileHashMapCommands.put("use", new UseCommand());
        String[] tableNames = new File(dbDir).list();
        for (String s : tableNames) {
            Path tableName = Paths.get(dbDir).resolve(s);
            if (Files.isDirectory(tableName)) {
                tables.put(s, new FileMap(tableName.toString()));
            }
        }

        if (args.length == 0) {
            while (true) {
                System.out.print(System.getProperty("user.dir") + "$ ");
                Scanner scanner = new Scanner(System.in);
                if (!scanner.hasNextLine()) {
                    break;
                }
                for (String s : scanner.nextLine().split(";")) {
                    String[] argv = s.trim().split("\\s+");
                    String curCommand = argv[0];
                    if (curCommand.equals("")) {
                        //System.out.print(System.getProperty("user.dir") + "$ ");
                        continue;
                    }
                    if (multiFileHashMapCommands.get(curCommand) == null) {
                        System.out.println(curCommand
                                + ": command not found");
                        errorOccurred = true;
                        continue;
                    }
                    if (!multiFileHashMapCommands.get(curCommand).execute(
                            argv)) {
                        errorOccurred = true;
                    }
                }
            }
        } else {
            StringBuilder joinedArgs = new StringBuilder();
            for (String s : args) {
                joinedArgs.append(s);
                joinedArgs.append(" ");
            }
            String[] argsPool = joinedArgs.toString().split(";");
            for (String s : argsPool) {
                String[] argv = s.trim().split("\\s+");
                String curCommand = argv[0];
                if (curCommand.equals("")) {
                    continue;
                }
                if (multiFileHashMapCommands.get(curCommand) == null) {
                    System.out.println(curCommand
                            + ": command not found");
                    errorOccurred = true;
                    continue;
                }
                if (!multiFileHashMapCommands.get(curCommand).execute(
                        argv)) {
                    errorOccurred = true;
                }
            }
        }
        MultiFileHashMap.save();
        return !errorOccurred;
    }

    public static void save() {
        for (String tableName : tables.keySet()) {
            FileMap table = tables.get(tableName);
            table.save();
        }
    }
}
