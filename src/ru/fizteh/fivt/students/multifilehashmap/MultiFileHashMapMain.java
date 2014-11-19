package ru.fizteh.fivt.students.multifilehashmap;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Lenovo on 30.09.2014.
 */
public class MultiFileHashMapMain {

    public static void main(String[] args) {

        try {
            rootPath = Paths.get(System.getProperty("user.dir")).resolve(System.getProperty("fizteh.db.dir"));
            checkPath(rootPath);
            multiFileHashMap = new MultiFileHashMap(rootPath);
        } catch (NullPointerException e) {
            System.err.println("Can't open file");
            System.exit(1);
        } catch (MyException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }

        if (args.length == 0) {
            MultiFileHashMapMain.interfaceMode();
        } else {
            MultiFileHashMapMain.packageMode(args);
        }
    }

    static Path rootPath;
    static MultiFileHashMap multiFileHashMap;
    static FileMap currentTable;

    private static void checkPath(Path startingPath) throws MyException {
        if (!startingPath.toFile().exists()) {
            throw new MyException("Starting path does not exist");
        }
        if (!startingPath.toFile().isDirectory()) {
            throw new MyException("Starting path is not a directory");
        }
    }

    private static void switchCommand(String[] command) throws MyException {

        Map<String, Commands> commands = new HashMap<String, Commands>();
        Map<String, CommandsForTables> commandsForTables = new HashMap<String, CommandsForTables>();
        commands.put(new PutCommand().getName(), new PutCommand());
        commands.put(new GetCommand().getName(), new GetCommand());
        commands.put(new ListCommand().getName(), new ListCommand());
        commands.put(new RemoveCommand().getName(), new RemoveCommand());
        commandsForTables.put(new ExitCommand().getName(), new ExitCommand());
        commandsForTables.put(new UseCommand().getName(), new UseCommand());
        commandsForTables.put(new ShowTablesCommand().getName(), new ShowTablesCommand());
        commandsForTables.put(new DropCommand().getName(), new DropCommand());
        commandsForTables.put(new CreateCommand().getName(), new CreateCommand());

        try {
            commandsForTables.get(command[0]).execute(command, multiFileHashMap);
        } catch (NullPointerException e) {
            try {
                if (currentTable != null) {
                    commands.get(command[0]).execute(command, currentTable);
                } else {
                    throw new MyException("no table");
                }
            } catch (NullPointerException ex) {
                throw new MyException("No such command");
            }
        }
    }

    private static void interfaceMode() {
        Scanner input = new Scanner(System.in);
        System.out.print("$ ");
        while (true) {
            if (!input.hasNextLine()) {
                System.exit(0);
            }
            String com = input.nextLine();
            if (com.length() == 0) {
                System.out.print("$ ");
                continue;
            }
            String[] commands = com.split(";");
            for (String string: commands) {
                try {
                    String[] command = string.trim().split("\\s+");
                    if (command.length == 1 && command[0].length() == 0) {
                        continue;
                    } else {
                        switchCommand(command);
                    }
                } catch (MyException e) {
                    System.out.println(e.getMessage());
                }
            }
            System.out.print("$ ");
        }
    }

    private static void packageMode(String[] args) {
        LinkedHashSet<String> com = new LinkedHashSet<String>();

        String s = "";
        for (String string : args) {

            s = s + string + " ";

        }
        String[] commands = s.split(";");

        for (String string : commands) {
            String[] command = string.trim().split("\\s+");
            try {
                if (command.length == 1 && command[0].length() == 0) {
                    continue;
                } else {
                    switchCommand(command);
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
                System.exit(1);
            }
        }
    }
}
