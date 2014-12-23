package ru.fizteh.fivt.students.SibgatullinDamir.parallel;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Lenovo on 30.09.2014.
 */
public class ParallelMain {

    public static void main(String[] args) {

        try {
            rootPath = Paths.get(System.getProperty("user.dir")).resolve(System.getProperty("fizteh.db.dir"));
            checkPath(rootPath);
            MyTableProviderFactory factory = new MyTableProviderFactory();
            provider = (MyTableProvider) factory.create(rootPath.toString());
        } catch (NullPointerException e) {
            System.err.println("Can't open file");
            System.exit(1);
        } catch (MyException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Can't create factory");
            System.exit(1);
        }

        if (args.length == 0) {
            ParallelMain.interfaceMode();
        } else {
            ParallelMain.packageMode(args);
        }
    }

    static Path rootPath;
    static MyTableProvider provider;

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
        commands.put(new CommitJUnitCommand().getName(), new CommitJUnitCommand());
        commands.put(new RollbackJUnitCommand().getName(), new RollbackJUnitCommand());
        commands.put(new SizeJUnitCommand().getName(), new SizeJUnitCommand());
        commandsForTables.put(new ExitCommand().getName(), new ExitCommand());
        commandsForTables.put(new UseCommand().getName(), new UseCommand());
        commandsForTables.put(new ShowTablesCommand().getName(), new ShowTablesCommand());
        commandsForTables.put(new DropCommand().getName(), new DropCommand());
        commandsForTables.put(new CreateCommand().getName(), new CreateCommand());

        try {
            commandsForTables.get(command[0]).execute(command, provider);
        } catch (NullPointerException e) {
            try {
                if (provider.usingTable != null) {
                    commands.get(command[0]).execute(command, provider.usingTable);
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

        StringBuilder str = new StringBuilder();
        for (String string : args) {
            str.append(string);
            str.append(" ");
        }
        String s = str.toString();
        String[] commands = s.split(";");

        for (String string : commands) {
            String[] command = string.trim().split("\\s+");
            try {
                if (command.length == 1 && command[0].isEmpty()) {
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
